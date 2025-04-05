import os
import requests
from bs4 import BeautifulSoup
import pandas as pd
import time

base_url = "https://thesdirect.com/collections/tisane-et-infusion"
product_list = []

# Function to get product details from individual product pages
def get_product_details(product_url):
    response = requests.get(product_url)
    soup = BeautifulSoup(response.content, 'html.parser')
    
    # Adjust this to match the correct HTML structure for ingredients or description
    ingredients_section = soup.find('div', class_='product-description')  # Example class
    if not ingredients_section:
        ingredients_section = soup.find('div', class_='product__details-content')  # Another possible class
    
    ingredients = ingredients_section.text.strip() if ingredients_section else "No details"
    return ingredients

# Pagination loop
for page in range(1, 6):  # Adjust this to match the total number of pages
    url = f"{base_url}?page={page}"
    response = requests.get(url)
    soup = BeautifulSoup(response.content, 'html.parser')

    # Extract product links
    products = soup.find_all('div', class_='t4s-product')
    for product in products:
        name = product.find('h3', class_='t4s-product-title').text.strip()
        price = product.find('div', class_='t4s-product-price').text.strip()
        product_link = product.find('a', class_='t4s-full-width-link')['href']
        product_url = f"https://thesdirect.com{product_link}"
        
        # Scrape details from the product page
        ingredients = get_product_details(product_url)
        
        product_list.append({
            'Product Name': name,
            'Price': price,
            'Ingredients': ingredients
        })
        
        # To avoid being blocked, add a small delay between requests
        time.sleep(1)

# Convert to DataFrame and save
df = pd.DataFrame(product_list)
df.to_csv('Detailed_Tisanes_et_Infusions_Products.csv', index=False)
print("Scraping completed. Detailed data saved.")
