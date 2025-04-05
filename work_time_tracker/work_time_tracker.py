import os
import json
from datetime import datetime, timedelta
import shutil
import hashlib
import getpass

# Constants
LOG_FILE = "work_time_log.json"
BACKUP_DIR = "backups"
MAX_BACKUPS = 5

# User authentication
def authenticate():
    stored_hash = None
    if os.path.exists("password.txt"):
        with open("password.txt", "r") as f:
            stored_hash = f.read().strip()
    
    if not stored_hash:
        print("First-time setup. Please create a password.")
        password = getpass.getpass("Enter new password: ")
        hashed = hashlib.sha256(password.encode()).hexdigest()
        with open("password.txt", "w") as f:
            f.write(hashed)
        print("Password set successfully.")
        return True
    
    attempt = getpass.getpass("Enter password: ")
    return hashlib.sha256(attempt.encode()).hexdigest() == stored_hash

# Backup function
def backup_log():
    if not os.path.exists(BACKUP_DIR):
        os.makedirs(BACKUP_DIR)
    
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_file = f"{BACKUP_DIR}/work_time_log_{timestamp}.json"
    shutil.copy2(LOG_FILE, backup_file)
    
    # Remove old backups if exceeding MAX_BACKUPS
    backups = sorted(os.listdir(BACKUP_DIR))
    while len(backups) > MAX_BACKUPS:
        os.remove(os.path.join(BACKUP_DIR, backups[0]))
        backups = backups[1:]

# Load existing data or create a new structure
def load_data():
    try:
        if os.path.exists(LOG_FILE):
            with open(LOG_FILE, "r") as file:
                return json.load(file)
    except json.JSONDecodeError:
        print("Error reading the log file. Creating a new one.")
    return {"entries": []}

# Save data to the JSON file
def save_data(data):
    try:
        with open(LOG_FILE, "w") as file:
            json.dump(data, file, indent=4)
        backup_log()
    except IOError:
        print("Error saving data. Please check file permissions.")

# Validate check-in/check-out sequence
def validate_entry(data, entry_type):
    if not data["entries"]:
        return entry_type == "Check In"
    return data["entries"][-1]["type"] != entry_type

# Check in function
def check_in():
    data = load_data()
    if not validate_entry(data, "Check In"):
        print("Error: Cannot check in before checking out.")
        return
    current_time = datetime.now().isoformat()
    data["entries"].append({"type": "Check In", "time": current_time})
    save_data(data)
    print(f"Checked in at {current_time}")

# Check out function
def check_out():
    data = load_data()
    if not validate_entry(data, "Check Out"):
        print("Error: Cannot check out before checking in.")
        return
    current_time = datetime.now().isoformat()
    data["entries"].append({"type": "Check Out", "time": current_time})
    save_data(data)
    print(f"Checked out at {current_time}")

# Calculate total time worked
def calculate_total_time():
    data = load_data()
    total_time = timedelta()
    last_check_in = None

    for entry in data["entries"]:
        if entry["type"] == "Check In":
            last_check_in = datetime.fromisoformat(entry["time"])
        elif entry["type"] == "Check Out":
            if last_check_in:
                check_out_time = datetime.fromisoformat(entry["time"])
                total_time += check_out_time - last_check_in
                last_check_in = None

    print(f"Total time worked: {total_time}")

# Show the log of all check-ins and check-outs
def show_log():
    data = load_data()
    print("\nWork Time Log:\n")
    for entry in data["entries"]:
        print(f"{entry['type']}: {entry['time']}")

# Clear today's entries
def clear_today_entries():
    data = load_data()
    today_date = datetime.now().strftime('%Y-%m-%d')
    data["entries"] = [entry for entry in data["entries"] if not entry["time"].startswith(today_date)]
    save_data(data)
    print(f"Cleared today's entries (Date: {today_date}).")

# Generate weekly report
def generate_weekly_report():
    data = load_data()
    today = datetime.now().date()
    start_of_week = today - timedelta(days=today.weekday())
    end_of_week = start_of_week + timedelta(days=6)

    weekly_total = timedelta()
    daily_totals = {(start_of_week + timedelta(days=i)).strftime("%Y-%m-%d"): timedelta() for i in range(7)}

    last_check_in = None
    for entry in data["entries"]:
        entry_date = datetime.fromisoformat(entry["time"]).date()
        if start_of_week <= entry_date <= end_of_week:
            if entry["type"] == "Check In":
                last_check_in = datetime.fromisoformat(entry["time"])
            elif entry["type"] == "Check Out" and last_check_in:
                check_out_time = datetime.fromisoformat(entry["time"])
                duration = check_out_time - last_check_in
                weekly_total += duration
                daily_totals[entry_date.strftime("%Y-%m-%d")] += duration
                last_check_in = None

    print(f"\nWeekly Report ({start_of_week} to {end_of_week}):")
    for date, total in daily_totals.items():
        print(f"{date}: {total}")
    print(f"\nTotal for the week: {weekly_total}")

# Main function
def main():
    if not authenticate():
        print("Authentication failed. Exiting.")
        return

    while True:
        print("\nWork Time Tracker")
        print("1. Check In")
        print("2. Check Out")
        print("3. Show Log")
        print("4. Calculate Total Time Worked")
        print("5. Clear Today's Entries")
        print("6. Generate Weekly Report")
        print("7. Exit")
        
        choice = input("Enter your choice: ")

        if choice == "1":
            check_in()
        elif choice == "2":
            check_out()
        elif choice == "3":
            show_log()
        elif choice == "4":
            calculate_total_time()
        elif choice == "5":
            clear_today_entries()
        elif choice == "6":
            generate_weekly_report()
        elif choice == "7":
            break
        else:
            print("Invalid choice, please try again.")

if __name__ == "__main__":
    main()