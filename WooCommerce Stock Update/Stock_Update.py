import requests
import pandas as pd
import os
from woocommerce import API
import logging
import sys
import traceback

# Configure logging
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s: %(message)s',
    handlers=[
        logging.FileHandler('stock_update.log', mode='w'),
        logging.StreamHandler(sys.stdout)
    ]
)

class StockUpdater:
    def __init__(self, login_url, email, password, wc_api_url, wc_api_key, wc_api_secret):
        """
        Initialize the stock updater with login and API credentials
        """
        self.login_url = login_url
        self.email = email
        self.password = password
        
        # Setup WooCommerce API connection
        self.wcapi = API(
            url=wc_api_url,
            consumer_key=wc_api_key,
            consumer_secret=wc_api_secret,
            version="wc/v3",
            timeout=30  # Increased timeout
        )
        
        # Session for web interactions
        self.session = requests.Session()
        
    def login(self):
        """
        Login to the website and get session cookies
        """
        try:
            login_data = {
                'username': self.email,
                'password': self.password,
                'loginBtn': 'Login'
            }
            
            response = self.session.post(self.login_url, data=login_data)
            
            if response.status_code == 200:
                logging.info("Login successful")
                return True
            else:
                logging.error(f"Login failed with status code {response.status_code}")
                return False
        except Exception as e:
            logging.error(f"Login error: {e}")
            return False
    
    def download_csv(self, download_url, output_path='pricelist.csv'):
        """
        Download CSV file after login
        """
        try:
            csv_response = self.session.get(download_url)
            
            if csv_response.status_code == 200:
                with open(output_path, 'wb') as f:
                    f.write(csv_response.content)
                logging.info(f"CSV downloaded successfully to {output_path}")
                return output_path
            else:
                logging.error(f"CSV download failed with status code {csv_response.status_code}")
                return None
        except Exception as e:
            logging.error(f"CSV download error: {e}")
            return None
    
    def parse_csv_stock(self, csv_path):
        """
        Parse CSV and extract SKU and stock information
        """
        try:
            # Read CSV file
            df = pd.read_csv(csv_path, header=None)
            
            # Assign column names based on the provided structure
            df.columns = ['Product', 'Stock', 'SKU', 'Price', 'EAN']
            
            # Clean and parse stock column
            def parse_stock(stock_str):
                if isinstance(stock_str, str):
                    if stock_str == '50+':
                        return 50
                    elif stock_str.startswith('<'):
                        return int(stock_str.replace('<', ''))
                return 0
            
            # Apply stock parsing
            df['Stock'] = df['Stock'].apply(parse_stock)
            
            # Select and clean columns
            df = df[['SKU', 'Stock']]
            
            # Remove any rows with empty SKU
            df = df[df['SKU'].notna() & (df['SKU'] != '')]
            
            logging.info(f"Parsed {len(df)} products from CSV")
            return df
        except Exception as e:
            logging.error(f"CSV parsing error: {e}")
            return pd.DataFrame(columns=['SKU', 'Stock'])
    
    def get_existing_products(self):
        """
        Retrieve existing products from WooCommerce
        """
        try:
            # Fetch all products with SKU
            products = []
            page = 1
            while True:
                logging.debug(f"Fetching products - Page {page}")
                
                response = self.wcapi.get('products', params={
                    'per_page': 100, 
                    'page': page,
                    'status': 'publish'  # Only fetch published products
                })
                
                # Log raw response details
                logging.debug(f"API Response Status Code: {response.status_code}")
                
                # Check for API errors
                if response.status_code != 200:
                    logging.error(f"API Error: {response.status_code}")
                    logging.error(f"Response Content: {response.text}")
                    break
                
                # Parse JSON response
                batch = response.json()
                
                # Log batch details
                logging.debug(f"Batch size: {len(batch)}")
                
                if not batch:
                    break
                
                # Extract product details with full logging
                for p in batch:
                    if p.get('sku'):
                        logging.debug(f"Product SKU: {p.get('sku')}, ID: {p.get('id')}")
                
                products.extend(batch)
                page += 1
            
            # Create SKU to ID mapping
            existing_skus = {p.get('sku', ''): p['id'] for p in products if p.get('sku')}
            
            logging.info(f"Found {len(existing_skus)} existing products with SKUs")
            
            # Log first few SKUs for verification
            logging.debug("Sample SKUs:")
            for sku, product_id in list(existing_skus.items())[:10]:
                logging.debug(f"SKU: {sku}, Product ID: {product_id}")
            
            return existing_skus
        except Exception as e:
            logging.error(f"Comprehensive product fetch error: {e}")
            logging.error(f"Error type: {type(e)}")
            logging.error(traceback.format_exc())
            return {}
    
    def update_stock(self, stock_df):
        """
        Update stock for existing products with detailed logging
        """
        existing_products = self.get_existing_products()
        updated_count = 0
        new_products = []
        
        # Detailed logging of stock update process
        logging.info(f"Starting stock update for {len(stock_df)} products")
        
        for idx, row in stock_df.iterrows():
            sku = str(row['SKU']).strip()
            stock = int(row['Stock'])
            
            if sku in existing_products:
                try:
                    # Prepare update payload
                    payload = {
                        'stock_quantity': stock,
                        'manage_stock': True
                    }
                    
                    # Log update details
                    logging.debug(f"Updating SKU: {sku}, Product ID: {existing_products[sku]}, New Stock: {stock}")
                    
                    # Perform stock update
                    response = self.wcapi.put(
                        f"products/{existing_products[sku]}", 
                        payload
                    )
                    
                    # Check update response
                    if response.status_code in [200, 201]:
                        updated_count += 1
                        logging.info(f"Successfully updated stock for SKU {sku} to {stock}")
                    else:
                        logging.error(f"Failed to update stock for SKU {sku}")
                        logging.error(f"Response Status: {response.status_code}")
                        logging.error(f"Response Content: {response.text}")
                
                except Exception as update_error:
                    logging.error(f"Stock update error for SKU {sku}: {update_error}")
                    logging.error(traceback.format_exc())
            else:
                # Track new products
                new_products.append(row)
                logging.debug(f"New product SKU: {sku}")
        
        # Create DataFrame of new products
        new_products_df = pd.DataFrame(new_products)
        
        logging.info(f"Stock update complete. Updated {updated_count} products")
        logging.info(f"Found {len(new_products)} new products")
        
        return updated_count, new_products_df
    
    def run(self, csv_download_url, output_dir='./'):
        """
        Main workflow method with comprehensive error handling
        """
        try:
            # Ensure output directory exists
            os.makedirs(output_dir, exist_ok=True)
            
            # Login
            if not self.login():
                logging.error("Login failed. Exiting.")
                return
            
            # Download CSV
            csv_path = self.download_csv(csv_download_url, os.path.join(output_dir, 'pricelist.csv'))
            if not csv_path:
                logging.error("CSV download failed. Exiting.")
                return
            
            # Parse CSV
            stock_df = self.parse_csv_stock(csv_path)
            
            # Update stock
            updated_count, new_products_df = self.update_stock(stock_df)
            
            # Save new products to CSV
            if not new_products_df.empty:
                new_products_path = os.path.join(output_dir, 'new_products.csv')
                new_products_df.to_csv(new_products_path, index=False)
                logging.info(f"New products saved to {new_products_path}")
        
        except Exception as e:
            logging.error(f"Unexpected error in stock update process: {e}")
            logging.error(traceback.format_exc())

# Usage example
if __name__ == "__main__":
    updater = StockUpdater(
        login_url="https://www.bluefinmobileshop.com/login/",
        email="ayman@jungletech.fr",
        password="HxUgDwu",
        wc_api_url="https://lightgreen-grouse-876378.hostingersite.com/wp-json/wc/v3",
        wc_api_key="ck_3e57ba3338b5b29e50eb450f0f746d810babb5d8",
        wc_api_secret="cs_c304669ef6f0a3eb2915f96bd827391910516bc6"
    )
    
    updater.run(csv_download_url="https://www.bluefinmobileshop.com/pricelist-download/CSV/")