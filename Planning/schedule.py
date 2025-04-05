import datetime

def update_schedule(wake_up_time, is_shopping_day):
    # Parse wake-up time
    wake_up = datetime.datetime.strptime(wake_up_time, "%H:%M")
    
    # Determine lunch time
    lunch_time = datetime.datetime.strptime("13:00", "%H:%M") if wake_up.hour >= 10 else datetime.datetime.strptime("12:00", "%H:%M")
    
    # Calculate time slots
    current_time = wake_up
    schedule = []
    
    # Morning routine (1 hour)
    end_time = current_time + datetime.timedelta(hours=1)
    schedule.append((current_time.strftime("%H:%M"), end_time.strftime("%H:%M"), "Wake up and morning routine"))
    current_time = end_time
    
    # Study sessions before lunch
    while current_time < lunch_time:
        if (lunch_time - current_time).total_seconds() / 3600 >= 1.5:
            end_time = current_time + datetime.timedelta(hours=1, minutes=30)
            schedule.append((current_time.strftime("%H:%M"), end_time.strftime("%H:%M"), "Study Session"))
            current_time = end_time
        else:
            schedule.append((current_time.strftime("%H:%M"), lunch_time.strftime("%H:%M"), "Short Study Session"))
            break
    
    # Lunch break
    end_time = lunch_time + datetime.timedelta(hours=1)
    schedule.append((lunch_time.strftime("%H:%M"), end_time.strftime("%H:%M"), "Lunch break"))
    current_time = end_time
    
    # Afternoon sessions
    end_time = datetime.datetime.strptime("18:00", "%H:%M") if is_shopping_day else datetime.datetime.strptime("19:30", "%H:%M")
    while current_time < end_time:
        if (end_time - current_time).total_seconds() / 3600 >= 1.5:
            next_time = current_time + datetime.timedelta(hours=1, minutes=30)
            schedule.append((current_time.strftime("%H:%M"), next_time.strftime("%H:%M"), "Study Session"))
            current_time = next_time
        else:
            schedule.append((current_time.strftime("%H:%M"), end_time.strftime("%H:%M"), "Short Study Session"))
            break
    
    # Evening activity
    if is_shopping_day:
        schedule.append((end_time.strftime("%H:%M"), "End", "Shopping"))
    else:
        schedule.append((end_time.strftime("%H:%M"), "21:00", "Break"))
        schedule.append(("21:00", "End", "Evening Study Session"))
    
    # Generate Markdown
    markdown = "# Updated Daily Schedule 📅\n\n"
    markdown += "| Time ⏰ | Activity 🚀 |\n"
    markdown += "|:-------------|:--------------------------|\n"
    
    for start, end, activity in schedule:
        if end == "End":
            markdown += f"| {start} onwards | {activity} |\n"
        else:
            markdown += f"| {start} - {end} | {activity} |\n"
    
    markdown += "\n**Tags**: #planning #dailyschedule #productivity\n\n"
    markdown += "---\n*Note: This schedule is dynamically generated based on your wake-up time and daily plans.*\n"
    
    # Write to file
    with open("daily_schedule.md", "w") as f:
        f.write(markdown)
    
    print("Schedule updated and saved to daily_schedule.md")

# Get user input
wake_up_time = input("What time do you wake up? (HH:MM format): ")
is_shopping_day = input("Is it a shopping day? (yes/no): ").lower() == 'yes'

# Update the schedule
update_schedule(wake_up_time, is_shopping_day)