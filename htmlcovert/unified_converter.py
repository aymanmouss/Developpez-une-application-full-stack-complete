from bs4 import BeautifulSoup
import os

class HTMLConverter:
    def __init__(self):
        self.soup = BeautifulSoup('', 'html.parser')
    
    def read_html_file(self, filename, input_dir='input'):
        """Read HTML file from input directory"""
        file_path = os.path.join(input_dir, filename)
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                return file.read()
        except FileNotFoundError:
            print(f"Warning: {filename} not found in {input_dir} directory")
            return None

    def format_tag_url(self, tag_text):
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
    
    def convert_datasheet(self, html_content):
        """Convert datasheet section"""
        soup = BeautifulSoup(html_content, 'html.parser')
        
        container = self.soup.new_tag('div')
        container['class'] = 'product-datasheet'
        
        table = self.soup.new_tag('table')
        table['class'] = 'datasheet-list'
        
        tbodies = soup.find_all('tbody')
        for tbody in tbodies[1:]:  # Skip first tbody
            new_tbody = self.soup.new_tag('tbody')
            rows = tbody.find_all('tr')
            
            for row in rows:
                new_row = self.soup.new_tag('tr')
                new_row['class'] = row.get('class', [])
                
                if row.find('th'):
                    th = row.find('th')
                    new_th = self.soup.new_tag('th')
                    new_th['colspan'] = '2'
                    new_th.string = th.get_text(strip=True)
                    new_row.append(new_th)
                else:
                    key_cell = row.find('td', class_='datasheet-listItemKey')
                    value_cell = row.find('td', class_='datasheet-listItemValue')
                    
                    if key_cell and value_cell:
                        new_key = self.soup.new_tag('td')
                        new_key['class'] = 'datasheet-listItemKey'
                        new_key.string = key_cell.get_text(strip=True)
                        
                        new_value = self.soup.new_tag('td')
                        new_value['class'] = 'datasheet-listItemValue'
                        new_value.string = value_cell.get_text(strip=True)
                        
                        new_row.append(new_key)
                        new_row.append(new_value)
                
                new_tbody.append(new_row)
            
            table.append(new_tbody)
        
        # Add toggle button
        tfoot = self.soup.new_tag('tfoot')
        tfoot_row = self.soup.new_tag('tr')
        tfoot_cell = self.soup.new_tag('td')
        tfoot_cell['colspan'] = '2'
        
        gradient = self.soup.new_tag('div')
        gradient['class'] = 'datasheet-gradient'
        
        button = self.soup.new_tag('button')
        button['class'] = 'datasheet-toggle-btn'
        button['type'] = 'button'
        
        show_more = self.soup.new_tag('span')
        show_more['class'] = 'show-more'
        show_more.string = 'Afficher tous les détails'
        
        show_less = self.soup.new_tag('span')
        show_less['class'] = 'show-less'
        show_less.string = 'Afficher moins de détails'
        
        button.append(show_more)
        button.append(show_less)
        tfoot_cell.append(gradient)
        tfoot_cell.append(button)
        tfoot_row.append(tfoot_cell)
        tfoot.append(tfoot_row)
        table.append(tfoot)
        
        container.append(table)
        return container
    
    def convert_pros_cons(self, html_content):
        """Convert pros and cons section"""
        soup = BeautifulSoup(html_content, 'html.parser')
        
        container = self.soup.new_tag('div')
        container['class'] = 'pros-cons-container'
        
        # Add title
        title = self.soup.new_tag('h2')
        title['class'] = 'pros-cons-title'
        title.string = 'Points forts et points faibles'
        container.append(title)
        
        # Add description
        desc = self.soup.new_tag('p')
        desc['class'] = 'pros-cons-description'
        desc.string = "La rédaction d'idealo a résumé pour vous les informations les plus importantes."
        container.append(desc)
        
        # Create flex container
        flex_container = self.soup.new_tag('div')
        flex_container['class'] = 'pros-cons-flex'
        
        original_content = soup.find('div', class_='pros-cons')
        if original_content:
            # Process pros
            pros_box = original_content.find('div', class_='box')
            if pros_box:
                pros_card = self.create_pros_cons_card('pros', pros_box)
                flex_container.append(pros_card)
            
            # Process cons
            cons_box = pros_box.find_next_sibling('div', class_='box') if pros_box else None
            if cons_box:
                cons_card = self.create_pros_cons_card('cons', cons_box)
                flex_container.append(cons_card)
        
        container.append(flex_container)
        return container
    
    def create_pros_cons_card(self, card_type, original_box):
        """Helper function to create pros/cons card"""
        card = self.soup.new_tag('div')
        card['class'] = f'{card_type}-card'
        
        header = self.soup.new_tag('h3')
        header['class'] = 'card-header'
        header.string = 'Points forts' if card_type == 'pros' else 'Points faibles'
        card.append(header)
        
        list_elem = self.soup.new_tag('ul')
        list_elem['class'] = f'{card_type}-list'
        
        items = original_box.find_all('li')
        for item in items:
            li = self.soup.new_tag('li')
            li.string = item.get_text(strip=True)
            list_elem.append(li)
        
        card.append(list_elem)
        return card
    
    def convert_tags(self, html_content):
        """Convert tags section with specific URL format"""
        soup = BeautifulSoup(html_content, 'html.parser')
        
        container = self.soup.new_tag('div')
        container['class'] = 'product-tags-container'
        
        # Add title
        original_title = soup.find('div', class_='productFurtherTags-title')
        title_text = original_title.h3.get_text(strip=True) if original_title and original_title.h3 else "Autres mots clés pour le produit"
        
        title = self.soup.new_tag('h2')
        title['class'] = 'product-tags-title'
        title.string = title_text
        container.append(title)
        
        # Create tags wrapper
        tags_wrapper = self.soup.new_tag('div')
        tags_wrapper['class'] = 'product-tags-wrapper'
        
        # Process tags
        original_tags = soup.find_all('a', class_='productFurtherTags-tag')
        for tag in original_tags:
            new_tag = self.soup.new_tag('a')
            new_tag['class'] = 'product-tag'
            
            # Get tag text and format URL
            tag_text = tag.get_text(strip=True)
            tag_url = self.format_tag_url(tag_text)
            new_tag['href'] = f"https://unnov.fr/product-tag/{tag_url}"
            
            new_tag.string = tag_text
            tags_wrapper.append(new_tag)
        
        container.append(tags_wrapper)
        return container
    
    def combine_sections(self, datasheet_html, pros_cons_html, tags_html):
        """Combine all sections into one HTML document"""
        main_container = self.soup.new_tag('div')
        main_container['class'] = 'product-content-container'
        
        # 1. Pros and Cons section first
        if pros_cons_html:
            pros_cons_section = self.convert_pros_cons(pros_cons_html)
            main_container.append(pros_cons_section)
        
        # 2. Datasheet table second
        if datasheet_html:
            datasheet_section = self.convert_datasheet(datasheet_html)
            main_container.append(datasheet_section)
        
        # 3. Tags section last
        if tags_html:
            tags_section = self.convert_tags(tags_html)
            main_container.append(tags_section)
        
        return main_container.prettify()

def main():
    converter = HTMLConverter()
    
    # Read input files
    datasheet_html = converter.read_html_file('datasheet.html')
    pros_cons_html = converter.read_html_file('pros_cons.html')
    tags_html = converter.read_html_file('tags.html')
    
    # Combine and convert
    combined_html = converter.combine_sections(datasheet_html, pros_cons_html, tags_html)
    
    # Write output
    output_dir = 'output'
    os.makedirs(output_dir, exist_ok=True)
    with open(os.path.join(output_dir, 'combined.html'), 'w', encoding='utf-8') as f:
        f.write(combined_html)
    
    print("Conversion completed successfully!")

if __name__ == "__main__":
    main()