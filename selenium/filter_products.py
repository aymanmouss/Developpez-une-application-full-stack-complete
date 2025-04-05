import pandas as pd
import re
from datetime import datetime
import logging

# Set up logging
logging.basicConfig(
    filename=f'product_updates_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log',
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)

def clean_price(price):
    """Clean price value and remove currency symbol"""
    if pd.isna(price) or str(price).strip() == '':
        return "0.00"
    
    try:
        price_str = str(price).strip()
        # Remove currency symbols and non-numeric characters except . and ,
        price_str = re.sub(r'[^\d.,]', '', price_str)
        
        # Handle different decimal separators
        if ',' in price_str and '.' not in price_str:
            price_str = price_str.replace(',', '.')
        
        # Handle multiple decimal points
        parts = price_str.split('.')
        if len(parts) > 2:
            price_str = ''.join(parts[:-1]) + '.' + parts[-1]
        
        price_num = float(price_str)
        return f"{price_num:.2f}"
    except (ValueError, TypeError):
        return "0.00"

def map_stock(stock_str):
    """Maps stock string to a numerical value"""
    if pd.isna(stock_str):
        return 0
    
    stock_str = str(stock_str).strip().lower()
    
    # Map text-based stock levels to numeric values
    stock_mapping = {
        "50+": 50,
        "<50": 49,
        "<40": 39,
        "<30": 29,
        "<20": 19,
        "<10": 9,
        "<5": 4
    }
    
    for key, value in stock_mapping.items():
        if key.lower() in stock_str:
            return value
            
    # Try to extract numeric value if present
    numbers = re.findall(r'\d+', stock_str)
    if numbers:
        return int(numbers[0])
            
    return 0

def get_product_category(title, sku):
    """Determine the product category based on SKU prefix and title"""
    title = str(title).lower()
    sku = str(sku)
    
    # Map SKU prefixes to their categories
    sku_categories = {
        '1003': 'phone',    # Xiaomi
        '1009': 'phone',    # Samsung Phones
        '1015': 'phone',    # Motorola
        '1028': 'phone',    # Apple iPhone
        '1042': ('phone', ['armor pad', 'tab']),  # Ulefone - check title for tablets
        '2001': 'laptop',   # Laptops
        '2002': 'tablet',   # iPads
        '2005': 'tablet',   # Samsung Tablets
        '2022': 'tablet',   # Ulefone Tablets
        '8001': 'smartwatch',  # Apple Watch
        '8004': 'smartwatch',  # Samsung Watch
        '13002': 'accessory',  # Wireless Chargers
        '30001': 'console',    # Nintendo
        '30003': 'console',    # PlayStation
        '43003': 'accessory',  # Earbuds
        '47001': 'accessory',  # Cables
        '48001': 'accessory',  # Chargers
        '48012': 'accessory',  # Charging stands
        '50009': 'accessory',  # Keyboards
        '73002': 'appliance'   # Home appliances
    }
    
    # Get prefix from SKU (part before first dash)
    sku_prefix = sku.split('-')[0] if '-' in sku else sku
    
    if sku_prefix in sku_categories:
        category = sku_categories[sku_prefix]
        # Handle special cases where category depends on title
        if isinstance(category, tuple):
            category, keywords = category
            if any(keyword in title for keyword in keywords):
                return 'tablet'
            return category
        return category
    
    return 'other'

def extract_base_model(title, category):
    """Extract the base model name from the title"""
    if pd.isna(title):
        return title
        
    title = str(title)
    # Remove text in parentheses
    title = re.sub(r'\([^)]*\)', '', title)
    
    if category == 'phone':
        # Handle different phone brands
        patterns = {
            'Samsung': r'Samsung\s+\w+\-?\w+\s+Galaxy\s+\w+',
            'Apple': r'Apple\s+iPhone\s+\d+(?:\s+\w+)?',
            'Ulefone': r'Ulefone\s+([\w\s]+?)(?:\s+Dual|$)',
            'Xiaomi': r'Xiaomi\s+([\w\s]+?)(?:\s+Dual|$)',
            'Motorola': r'Motorola\s+([\w\s]+?)(?:\s+Dual|$)'
        }
        
        for pattern in patterns.values():
            match = re.search(pattern, title)
            if match:
                return match.group(0).strip()
    
    elif category == 'tablet':
        # Extract tablet model before specifications
        match = re.search(r'^([^(]+?)(?:\s+\d+\.?\d*\s*(?:inch|"|\'\')|$)', title)
        if match:
            return match.group(1).strip()
    
    # Default to first part of title
    return title.split('(')[0].split('-')[0].strip()

def extract_color(title):
    """Extract color from the product title"""
    if pd.isna(title):
        return "N/A"
    
    title = str(title)
    
    # Define color patterns and variations
    colors = {
        'Black': ['black', 'midnight'],
        'White': ['white', 'titanium white'],
        'Gray': ['gray', 'grey', 'space grey', 'space gray', 'graphite', 'marble gray'],
        'Silver': ['silver', 'titanium silver', 'platinum silver'],
        'Gold': ['gold', 'rose gold'],
        'Blue': ['blue', 'navy', 'pacific blue', 'light blue', 'winter blue'],
        'Green': ['green', 'olive', 'forest green', 'light green', 'alpine green'],
        'Yellow': ['yellow', 'amber yellow'],
        'Purple': ['purple', 'deep purple', 'violet', 'cobalt violet'],
        'Pink': ['pink', 'light pink'],
        'Orange': ['orange'],
        'Red': ['red', 'product red'],
        'Titanium': ['titanium'],
        'Starlight': ['starlight']
    }
    
    title_lower = title.lower()
    for main_color, variants in colors.items():
        for variant in variants:
            pattern = rf'\b{variant}\b'
            if re.search(pattern, title_lower):
                return main_color
    
    return "N/A"

def extract_storage_ram(title):
    """Extract storage and RAM specifications"""
    if pd.isna(title):
        return {'storage': 'N/A', 'ram': 'N/A'}
    
    title = str(title)
    result = {'storage': 'N/A', 'ram': 'N/A'}
    
    # Look for storage (not followed by RAM)
    storage_patterns = [
        r'(\d+)(?:GB|TB)(?!\s+RAM)',
        r'(\d+)\s*(?:GB|TB)(?!\s+RAM)'
    ]
    
    for pattern in storage_patterns:
        storage_match = re.search(pattern, title)
        if storage_match:
            result['storage'] = f"{storage_match.group(1)}GB"
            break
    
    # Look for RAM
    ram_patterns = [
        r'(\d+)GB\s+RAM',
        r'(\d+)\s*GB\s*RAM',
        r'RAM\s+(\d+)GB'
    ]
    
    for pattern in ram_patterns:
        ram_match = re.search(pattern, title)
        if ram_match:
            result['ram'] = f"{ram_match.group(1)}GB"
            break
    
    return result

def is_variable_product(group_df):
    """Determine if a product should be variable based on its attributes"""
    if len(group_df) <= 1:
        return False
    
    # Check for varying attributes
    color_varies = len(group_df['color'].unique()) > 1
    storage_varies = len(group_df['storage'].unique()) > 1
    ram_varies = len(group_df['ram'].unique()) > 1
    
    # Category-specific logic
    if group_df['category'].iloc[0] in ['phone', 'tablet']:
        return color_varies or storage_varies or ram_varies
    
    return color_varies

def filter_excel_file(input_file, output_file):
    """Process the Excel file and create filtered CSV output"""
    print(f"\nStarting processing at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"Reading file: {input_file}")
    
    try:
        # Load Excel file
        df = pd.read_excel(input_file)
        
        # Rename columns
        column_mapping = {
            "Phone": "Title",
            "Art.-Nr.": "SKU",
            "In Stock": "in_stock",
            "Price": "Price",
            "EAN": "EAN"
        }
        df.rename(columns=column_mapping, inplace=True)
        
        # Clean data
        print("Cleaning data...")
        df['Title'] = df['Title'].fillna('')
        df['SKU'] = df['SKU'].fillna('')
        df['EAN'] = df['EAN'].fillna('')
        df['Price'] = df['Price'].apply(clean_price)
        
        # Filter EU Spec products
        filtered_df = df[df["Title"].str.contains("EU Spec", na=False)].copy()
        print(f"Found {len(filtered_df)} EU Spec products")
        
        # Process each record
        filtered_df['Stock'] = filtered_df['in_stock'].apply(map_stock)
        filtered_df['category'] = filtered_df.apply(
            lambda x: get_product_category(x['Title'], x['SKU']), 
            axis=1
        )
        
        # Extract specifications
        filtered_df['base_model'] = filtered_df.apply(
            lambda x: extract_base_model(x['Title'], x['category']), 
            axis=1
        )
        
        specs = filtered_df['Title'].apply(extract_storage_ram)
        filtered_df['storage'] = specs.apply(lambda x: x['storage'])
        filtered_df['ram'] = specs.apply(lambda x: x['ram'])
        filtered_df['color'] = filtered_df['Title'].apply(extract_color)
        
        # Group by base_model and determine variable products
        filtered_df['is_variable'] = False
        for model in filtered_df['base_model'].unique():
            model_group = filtered_df[filtered_df['base_model'] == model]
            if is_variable_product(model_group):
                filtered_df.loc[model_group.index, 'is_variable'] = True
        
        # Select and order columns
        output_columns = [
            'Title', 'SKU', 'Price', 'EAN', 'Stock',
            'base_model', 'is_variable', 'storage', 'ram', 'color',
            'category'
        ]
        output_df = filtered_df[output_columns].copy()
        
        # Save to CSV
        output_df.to_csv(output_file, index=False)
        print(f"Successfully saved output to {output_file}")
        
        # Print statistics
        print(f"\nProcessing completed at {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"Total products processed: {len(output_df)}")
        print(f"\nProduct categories found:")
        print(output_df['category'].value_counts())
        
        # Variable products statistics
        variable_products = output_df[output_df['is_variable'] == True]
        print(f"\nVariable products by category:")
        print(variable_products['category'].value_counts())
        
    except Exception as e:
        print(f"Error processing file: {str(e)}")
        raise

if __name__ == "__main__":
    input_file = "mobileshop-price-list.xlsx"
    output_file = "products.csv"
    filter_excel_file(input_file, output_file)