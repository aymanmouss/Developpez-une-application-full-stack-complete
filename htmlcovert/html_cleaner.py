from bs4 import BeautifulSoup
import os

def transform_specs_html(input_path, output_path):
    # Create directories if they don't exist
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    # Read the input file
    with open(input_path, 'r', encoding='utf-8') as file:
        content = file.read()
    
    # Parse HTML using BeautifulSoup
    soup = BeautifulSoup(content, 'html.parser')
    
    # Find the main container div
    main_div = soup.find('div', class_='carac-list')
    
    if main_div:
        # Find the ul containing all sections
        specs_ul = main_div.find('ul', class_='carac-list')
        
        if specs_ul:
            # Get all main li elements (sections)
            sections = specs_ul.find_all('li', recursive=False)
            
            # Create new HTML structure
            new_html = '<div class="unnov-specs">\n    <div class="specs-grid">\n'
            
            for section in sections:
                # Get section title
                title_div = section.find('div', class_='title')
                if title_div:
                    title = title_div.text.strip()
                    
                    # Get specifications
                    specs_ul = section.find('ul')
                    if specs_ul:
                        specs = specs_ul.find_all('li', class_='carac')
                        
                        if specs:
                            # Create section HTML
                            section_html = f'''        <!-- {title} Section -->
        <div class="specs-section">
            <h3 class="specs-title">{title.upper()}</h3>
            <div class="specs-content">\n'''
                            
                            # Add each specification
                            for spec in specs:
                                divs = spec.find_all('div')
                                if len(divs) >= 2:
                                    label = divs[0].text.strip()
                                    value = divs[1].text.strip()
                                    section_html += f'''                <div class="specs-row">
                    <span class="specs-label">{label}</span>
                    <span class="specs-value">{value}</span>
                </div>\n'''
                            
                            section_html += '            </div>\n        </div>\n\n'
                            new_html += section_html
            
            new_html += '    </div>\n</div>'
            
            # Write the transformed HTML to output file
            with open(output_path, 'w', encoding='utf-8') as file:
                file.write(new_html)
            return True
    
    return False

def process_specs_files():
    input_dir = 'input'
    output_dir = 'output'
    
    # Look for px-specs.html in input directory
    input_path = os.path.join(input_dir, 'px-specs.html')
    output_path = os.path.join(output_dir, 'px-specs.html')
    
    if os.path.exists(input_path):
        print(f"Processing {input_path}...")
        if transform_specs_html(input_path, output_path):
            print(f"Successfully created transformed file at {output_path}")
        else:
            print("Error: Could not parse the HTML structure correctly")
    else:
        print(f"Error: Could not find {input_path}")

if __name__ == "__main__":
    process_specs_files()