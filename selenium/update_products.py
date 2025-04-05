import pandas as pd
import requests
import logging
from datetime import datetime
from typing import Dict, Optional, Tuple, List
import os
import re
from requests.exceptions import RequestException

# Set up logging
logging.basicConfig(
    filename=f'product_updates_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log',
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)

# WooCommerce API credentials
WC_API_URL = os.getenv('WC_API_URL', 'https://lightgreen-grouse-876378.hostingersite.com/wp-json/wc/v3')
WC_API_KEY = os.getenv('WC_API_KEY', 'ck_3e57ba3338b5b29e50eb450f0f746d810babb5d8')
WC_API_SECRET = os.getenv('WC_API_SECRET', 'cs_c304669ef6f0a3eb2915f96bd827391910516bc6')

class ProductUpdateError(Exception):
    """Custom exception for product update errors"""
    pass

def api_request(method: str, endpoint: str, data: Optional[Dict] = None, params: Optional[Dict] = None) -> Tuple[bool, Dict]:
    """Wrapper for API requests with error handling"""
    try:
        response = requests.request(
            method,
            f"{WC_API_URL}/{endpoint}",
            auth=(WC_API_KEY, WC_API_SECRET),
            json=data if data else None,
            params=params if params else None,
            timeout=30  # Add timeout to prevent hanging
        )
        
        if response.status_code in [200, 201]:
            return True, response.json()
        
        logging.error(f"API Error: {response.status_code} - {response.text}")
        return False, {"error": response.text, "status": response.status_code}
    
    except RequestException as e:
        logging.error(f"Request Error: {str(e)}")
        return False, {"error": str(e)}

def find_product_by_sku_or_name(sku: Optional[str], base_model: str) -> Optional[Dict]:
    """Find a product by SKU or base model name"""
    if sku:
        success, response = api_request('GET', 'products', params={"sku": sku})
        if success and response:
            return response[0]
    
    if base_model:
        success, response = api_request('GET', 'products', params={"search": base_model})
        if success and response:
            # Check for exact base_model match
            for product in response:
                if product['name'].lower() == base_model.lower():
                    return product
    
    return None

def find_variation_by_sku(product_id: int, sku: str) -> Optional[Dict]:
    """Find existing variation by SKU with exact matching"""
    success, response = api_request(
        'GET',
        f'products/{product_id}/variations',
        params={'per_page': 100}
    )
    
    if success and response:
        for variation in response:
            if variation.get('sku') == sku:
                return variation
    return None

def process_variation(product_id: int, variation_data: Dict, is_update: bool = False, variation_id: Optional[int] = None) -> bool:
    """Process variation creation or update with improved validation"""
    try:
        # Validate required fields
        if not all(k in variation_data for k in ['regular_price', 'stock_quantity', 'sku']):
            raise ProductUpdateError("Missing required fields in variation data")
        
        # Format numeric values
        variation_data['regular_price'] = str(float(variation_data['regular_price']))
        variation_data['stock_quantity'] = int(variation_data['stock_quantity'])
        
        # Clean and validate attributes
        cleaned_attributes = []
        for attr in variation_data.get('attributes', []):
            if attr.get('option') and attr['option'] != 'N/A' and attr['option'].strip():
                attr['option'] = attr['option'].strip()
                cleaned_attributes.append(attr)
        
        if not cleaned_attributes:
            logging.warning(f"No valid attributes for SKU: {variation_data['sku']}")
            return False
            
        variation_data['attributes'] = cleaned_attributes
        variation_data['manage_stock'] = True  # Enable stock management
        
        # Check if variation already exists by exact SKU match
        existing_variation = None
        success, variations = api_request(
            'GET',
            f'products/{product_id}/variations',
            params={'per_page': 100}
        )
        
        if success and variations:
            for variation in variations:
                if variation.get('sku') == variation_data['sku']:
                    existing_variation = variation
                    break
        
        if existing_variation:
            # Update existing variation
            success, response = api_request(
                'PUT',
                f'products/{product_id}/variations/{existing_variation["id"]}',
                data=variation_data
            )
            if success:
                logging.info(f"Updated variation for SKU: {variation_data['sku']}")
        else:
            # Create new variation
            success, response = api_request(
                'POST',
                f'products/{product_id}/variations',
                data=variation_data
            )
            if success:
                logging.info(f"Created variation for SKU: {variation_data['sku']}")
        
        if not success:
            error_msg = response.get('error', 'Unknown error')
            raise ProductUpdateError(f"API Error: {error_msg}")
        
        return True
        
    except (ValueError, ProductUpdateError) as e:
        logging.error(f"Error processing variation for SKU {variation_data.get('sku', 'unknown')}: {str(e)}")
        return False

def create_variable_product(base_model: str, attributes: Dict[str, List[str]], category: str) -> Optional[Dict]:
    """Create a new variable product with category-specific handling"""
    cleaned_attributes = {}
    
    # For phones and tablets, include all valid attributes
    if category in ['phone', 'tablet']:
        for k, values in attributes.items():
            valid_values = [v.strip() for v in values if v and v != 'N/A' and v.strip()]
            if valid_values:
                cleaned_attributes[k] = list(set(valid_values))
    else:
        # For other categories, only include color if it varies
        if 'Color' in attributes:
            valid_colors = [v.strip() for v in attributes['Color'] if v and v != 'N/A' and v.strip()]
            if valid_colors:
                cleaned_attributes['Color'] = list(set(valid_colors))
    
    if not cleaned_attributes:
        logging.warning(f"No valid attributes for {base_model}")
        return None
    
    product_data = {
        "name": base_model,
        "type": "variable",
        "status": "publish",
        "manage_stock": True,
        "attributes": [
            {
                "name": name,
                "visible": True,
                "variation": True,
                "options": values
            }
            for name, values in cleaned_attributes.items()
        ]
    }
    
    success, response = api_request('POST', 'products', data=product_data)
    if success:
        logging.info(f"Created variable product: {base_model}")
        return response
    
    logging.error(f"Failed to create variable product {base_model}: {response.get('error')}")
    return None

def main():
    try:
        # Load and validate CSV file
        df = pd.read_csv("products.csv")
        required_columns = ['Price', 'SKU', 'base_model', 'Stock']
        
        if not all(col in df.columns for col in required_columns):
            raise ValueError("Missing required columns in CSV file")
        
        # Clean and prepare data
        df = df.fillna('')
        df['Price'] = pd.to_numeric(df['Price'], errors='coerce')
        df = df.dropna(subset=required_columns)
        
        # Track results
        results = {
            'updated': [],
            'created': [],
            'errors': []
        }
        
        # Process products by base_model
        for base_model in df['base_model'].unique():
            logging.info(f"\nProcessing {base_model}")
            
            try:
                group = df[df['base_model'] == base_model]
                is_variable = group['is_variable'].iloc[0]
                
                if not is_variable:
                    continue
                
                # Collect all attributes for this model
                attributes = {
                    'Storage': group['storage'].unique().tolist(),
                    'RAM': group['ram'].unique().tolist(),
                    'Color': group['color'].unique().tolist()
                }
                
                # Find or create parent product
                parent_product = find_product_by_sku_or_name(None, base_model)
                
                if not parent_product:
                    parent_product = create_variable_product(
                        base_model, 
                        attributes,
                        group['category'].iloc[0]
                    )
                    if not parent_product:
                        continue
                
                # Process each variation
                for _, row in group.iterrows():
                    variation_attributes = []
                    
                    # Add valid attributes
                    if row['storage'] and row['storage'] != 'N/A':
                        variation_attributes.append({"name": "Storage", "option": row['storage']})
                    if row['ram'] and row['ram'] != 'N/A':
                        variation_attributes.append({"name": "RAM", "option": row['ram']})
                    if row['color'] and row['color'] != 'N/A':
                        variation_attributes.append({"name": "Color", "option": row['color']})
                    
                    variation_data = {
                        "regular_price": str(float(row['Price'])),
                        "stock_quantity": int(row['Stock']),
                        "sku": row['SKU'],
                        "attributes": variation_attributes,
                        "manage_stock": True
                    }
                    
                    if process_variation(parent_product['id'], variation_data):
                        results['created'].append(row['SKU'])
                    else:
                        results['errors'].append({
                            "sku": row['SKU'],
                            "base_model": base_model
                        })
            
            except Exception as e:
                logging.error(f"Error processing {base_model}: {str(e)}")
                results['errors'].append({
                    "base_model": base_model,
                    "error": str(e)
                })
        
        # Print summary
        logging.info("\nSummary:")
        logging.info(f"Updated: {len(results['updated'])}")
        logging.info(f"Created: {len(results['created'])}")
        logging.info(f"Errors: {len(results['errors'])}")
        
        if results['errors']:
            logging.info("\nErrors encountered:")
            for error in results['errors']:
                logging.info(f"SKU: {error.get('sku', 'N/A')}, Model: {error.get('base_model', 'N/A')}")
    
    except Exception as e:
        logging.error(f"Fatal error: {str(e)}")
        raise

if __name__ == "__main__":
    main()