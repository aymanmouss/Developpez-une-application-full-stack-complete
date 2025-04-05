import re
import pandas as pd
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

# Configure WebDriver with a specific window size
options = webdriver.ChromeOptions()
options.add_argument("--window-size=1250,800")  # Set width to 1250px and height to 800px
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)

# Open the login page
driver.get("https://www.bluefinmobileshop.com/login/")

def extract_ean(detail_html):
    """
    Extracts the EAN number from the detailed product page.
    """
    match = re.search(r"EAN:\s?(\d+)", detail_html)
    return match.group(1) if match else None

try:
    print("Logging into the website...")
    # Login process
    email = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.NAME, "username"))
    )
    email.send_keys("ayman@jungletech.fr")

    password = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.NAME, "password"))
    )
    password.send_keys("HxUgDwu")

    login_button = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "loginBtn"))
    )
    login_button.click()
    print("Login successful.")

    # Wait for the "Samsung" link and click it
    print("Navigating to Samsung page...")
    samsung_link = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.XPATH, "//a[@href='/samsung/']"))
    )
    driver.execute_script("arguments[0].click();", samsung_link)  # Use JavaScript to click the link

    # Wait for the dropdown filter to load
    print("Applying EU Spec filter...")
    spec_filter = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.NAME, "specs"))
    )
    select = Select(spec_filter)
    select.select_by_visible_text("EU Spec")  # Select option by text

    # Wait for the table to reload after applying the filter
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, "table tbody tr"))
    )

    # Click "Load More" until all stock is loaded or max attempts are reached
    print("Loading all products...")
    max_clicks = 10  # Set a maximum number of clicks to prevent infinite loops
    previous_row_count = 0

    for _ in range(max_clicks):
        try:
            load_more_button = WebDriverWait(driver, 5).until(
                EC.presence_of_element_located((By.ID, "loadMore"))
            )
            driver.execute_script("arguments[0].scrollIntoView(true);", load_more_button)
            time.sleep(1)  # Small delay for smooth scrolling
            driver.execute_script("arguments[0].click();", load_more_button)  # JavaScript click
            time.sleep(3)  # Allow time for new data to load

            # Check if new rows have been added
            current_row_count = len(driver.find_elements(By.CSS_SELECTOR, "table tbody tr"))
            if current_row_count == previous_row_count:
                print("No new rows loaded. Exiting 'Load More' loop.")
                break
            previous_row_count = current_row_count
        except Exception as e:
            print(f"No more 'Load More' button found or an error occurred: {e}")
            break

    print("Extracting product data...")
    # Extract table data (Model, Price, Stock, EAN)
    table_rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")
    data = []
    for row_index, row in enumerate(table_rows):
        try:
            cells = row.find_elements(By.TAG_NAME, "td")
            if len(cells) >= 3:  # Ensure there are enough cells
                model = cells[0].text  # Product name or description
                price = cells[1].text  # Price
                stock_element = cells[2].find_element(By.TAG_NAME, "span")  # Stock span

                # Extract "Max. Qty" from the tooltip
                tooltip = stock_element.get_attribute("data-tooltip")
                try:
                    max_qty = int(tooltip.split("Max. Qty:")[1].strip().split("\n")[0])
                except Exception:
                    max_qty = 0  # Default to 0 if parsing fails

                # Click on the product row to get detailed information
                driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", row)
                time.sleep(1)  # Allow scrolling time
                try:
                    row.click()  # Attempt normal click
                except Exception:
                    driver.execute_script("arguments[0].click();", row)  # Use JavaScript to force click

                # Wait for the detailed view to load and extract details
                product_details = WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, ".product-details"))
                )
                detail_html = product_details.get_attribute("innerHTML")
                ean = extract_ean(detail_html)  # Extract the EAN

                # Add data to the list
                data.append({
                    "Model": model,
                    "Price": price,
                    "Stock": max_qty,
                    "EAN": ean if ean else "Not Found"
                })

                # Close the detailed view or go back to the product list
                driver.execute_script("window.history.back()")
                time.sleep(2)  # Allow time for the product list to reload
        except Exception as e:
            print(f"Error on row {row_index}: {e}")
            driver.save_screenshot(f"debug_row_{row_index}.png")

    print("Saving product data to CSV...")
    # Save data to a DataFrame and export to CSV
    if data:
        df = pd.DataFrame(data)
        df.to_csv("samsung_stock_with_ean.csv", index=False)
        print("Data saved to samsung_stock_with_ean.csv")
    else:
        print("No data found in the table.")

except Exception as e:
    print(f"Error encountered: {e}")
    driver.save_screenshot("debug_screenshot.png")  # Save a screenshot for debugging

finally:
    driver.quit()
