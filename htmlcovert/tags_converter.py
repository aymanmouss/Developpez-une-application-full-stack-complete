# tags_converter.py
from bs4 import BeautifulSoup
from html_utils import read_html_file, write_html_file, init_soup

def format_tag_url(tag_text):
    """Convert tag text to URL-friendly format"""
    # Convert to lowercase
    url = tag_text.lower()
    
    # Replace specific French characters
    replacements = {
        'é': 'e', 'è': 'e', 'ê': 'e', 'ë': 'e',
        'à': 'a', 'â': 'a', 'ä': 'a',
        'î': 'i', 'ï': 'i',
        'ô': 'o', 'ö': 'o',
        'û': 'u', 'ü': 'u',
        'ç': 'c'
    }
    for old, new in replacements.items():
        url = url.replace(old, new)
    
    # Replace spaces with hyphens
    url = url.replace(' ', '-')
    
    # Remove any special characters except hyphens, numbers, and commas
    url = ''.join(c for c in url if c.isalnum() or c in ['-', ','])
    
    # Remove multiple consecutive hyphens
    while '--' in url:
        url = url.replace('--', '-')
    
    # Remove leading and trailing hyphens
    url = url.strip('-')
    
    return url

def extract_tags_for_woocommerce(html_content):
    """Extract tags and save them in WooCommerce format"""
    soup = BeautifulSoup(html_content, 'html.parser')
    tags = soup.find_all('a', class_='productFurtherTags-tag')
    
    # Extract tag texts
    tag_texts = [tag.get_text(strip=True) for tag in tags]
    
    # Join tags with commas
    return ', '.join(tag_texts)

def convert_tags(html_content):
    """Convert tags HTML structure"""
    soup = init_soup(html_content)
    
    # Create the new container
    new_container = soup.new_tag('div')
    new_container['class'] = 'product-tags-container'
    
    # Add title
    title = create_title(soup)
    new_container.append(title)
    
    # Create tags wrapper
    tags_wrapper = create_tags_wrapper(soup)
    
    # Find all original tags
    original_tags = soup.find_all('a', class_='productFurtherTags-tag')
    
    # Process each tag
    for tag in original_tags:
        new_tag = create_tag(soup, tag)
        tags_wrapper.append(new_tag)
    
    new_container.append(tags_wrapper)
    return new_container.prettify()

def create_title(soup):
    """Create title element"""
    original_title = soup.find('div', class_='productFurtherTags-title')
    title_text = original_title.h3.get_text(strip=True) if original_title and original_title.h3 else "Autres mots clés pour le produit"
    
    title = soup.new_tag('h2')
    title['class'] = 'product-tags-title'
    title.string = title_text
    return title

def create_tags_wrapper(soup):
    """Create wrapper for tags"""
    wrapper = soup.new_tag('div')
    wrapper['class'] = 'product-tags-wrapper'
    return wrapper

def create_tag(soup, original_tag):
    """Create individual tag element"""
    new_tag = soup.new_tag('a')
    new_tag['class'] = 'product-tag'
    
    # Get tag text and format for URL
    tag_text = original_tag.get_text(strip=True)
    tag_url = format_tag_url(tag_text)
    
    # Create unnov.fr URL
    # new_tag['href'] = f"https://unnov.fr/product-tag/{tag_url}"
    new_tag['href'] = f"#"
    
    new_tag.string = tag_text
    return new_tag

if __name__ == "__main__":
    # Read input HTML
    html_content = read_html_file('tags.html')
    if html_content:
        # Convert HTML
        converted_html = convert_tags(html_content)
        # Write output HTML
        write_html_file(converted_html, 'tags.html')
        
        # Extract and save tags for WooCommerce
        woo_tags = extract_tags_for_woocommerce(html_content)
        with open('woocommerce_tags.txt', 'w', encoding='utf-8') as f:
            f.write(woo_tags)
        print("WooCommerce tags exported to woocommerce_tags.txt")