# html_utils.py
import os
from bs4 import BeautifulSoup

def read_html_file(filename, input_dir='input'):
    """Read HTML file from input directory"""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(script_dir, input_dir, filename)
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            return file.read()
    except FileNotFoundError:
        print(f"Warning: {filename} not found in {input_dir} directory")
        return None

def write_html_file(html_content, filename, output_dir='output'):
    """Write HTML content to output directory"""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(script_dir, output_dir)
    os.makedirs(output_path, exist_ok=True)
    
    file_path = os.path.join(output_path, filename)
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(html_content)
    print(f"Successfully wrote {filename}")

def init_soup(html_content):
    """Initialize BeautifulSoup object"""
    return BeautifulSoup(html_content, 'html.parser')