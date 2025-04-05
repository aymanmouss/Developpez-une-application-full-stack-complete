from bs4 import BeautifulSoup
import os

def convert_to_datasheet_format(input_html):
    # Parse the input HTML
    soup = BeautifulSoup(input_html, 'html.parser')
    
    # Create the output structure
    output_soup = BeautifulSoup('', 'html.parser')
    
    # Create the main div
    main_div = output_soup.new_tag('div', attrs={'class': 'product-datasheet'})
    
    # Create the main table
    table = output_soup.new_tag('table', attrs={'class': 'datasheet-list'})
    
    # Process categories in tbody
    categories = soup.find_all('li', class_='')
    
    # Define which categories should be open by default (first 4 categories)
    OPEN_CATEGORIES = ['général', 'technique', 'écran', 'stockage']
    
    for category in categories:
        # Create new tbody for each category
        tbody = output_soup.new_tag('tbody')
        
        # Get category title
        title = category.find('div', class_='title').text.strip()
        
        # Determine if this category should be collapsible
        is_collapsible = title.lower() not in OPEN_CATEGORIES
        
        # Create group header with conditional collapsible class
        group_classes = ['datasheet-listItem', 'datasheet-listItem--group']
        if is_collapsible:
            group_classes.append('datasheet-listItem--collapsible')
            
        group_tr = output_soup.new_tag('tr', attrs={'class': ' '.join(group_classes)})
        th = output_soup.new_tag('th', colspan="2")
        th.string = title.capitalize()
        group_tr.append(th)
        tbody.append(group_tr)
        
        # Get all properties in this category
        properties = category.find_all('li', class_='carac')
        
        for prop in properties:
            # Create property row with conditional collapsible class
            prop_classes = ['datasheet-listItem', 'datasheet-listItem--properties']
            if is_collapsible:
                prop_classes.append('datasheet-listItem--collapsible')
                
            prop_tr = output_soup.new_tag('tr', attrs={'class': ' '.join(prop_classes)})
            
            # Create key cell
            key_td = output_soup.new_tag('td', attrs={'class': 'datasheet-listItemKey'})
            key_td.string = prop.find('div', class_='mr-auto').text.strip()
            
            # Create value cell
            value_td = output_soup.new_tag('td', attrs={'class': 'datasheet-listItemValue'})
            value_td.string = prop.find('div', class_='ml-auto').text.strip()
            
            # Append cells to row
            prop_tr.append(key_td)
            prop_tr.append(value_td)
            
            # Append row to tbody
            tbody.append(prop_tr)
            
        # Append tbody to table
        table.append(tbody)
    
    # Create tfoot with toggle button
    tfoot = output_soup.new_tag('tfoot')
    tfoot_tr = output_soup.new_tag('tr')
    tfoot_td = output_soup.new_tag('td', colspan="2")
    
    # Add gradient div
    gradient_div = output_soup.new_tag('div', attrs={'class': 'datasheet-gradient'})
    tfoot_td.append(gradient_div)
    
    # Add toggle button
    toggle_btn = output_soup.new_tag('button', attrs={
        'class': 'datasheet-toggle-btn',
        'type': 'button'
    })
    
    # Add show more span
    show_more = output_soup.new_tag('span', attrs={'class': 'show-more'})
    show_more.string = 'Afficher tous les détails'
    toggle_btn.append(show_more)
    
    # Add show less span
    show_less = output_soup.new_tag('span', attrs={'class': 'show-less'})
    show_less.string = 'Afficher moins de détails'
    toggle_btn.append(show_less)
    
    # Assemble the footer
    tfoot_td.append(toggle_btn)
    tfoot_tr.append(tfoot_td)
    tfoot.append(tfoot_tr)
    table.append(tfoot)
    
    # Assemble the final structure
    main_div.append(table)
    output_soup.append(main_div)
    
    # Return the formatted HTML
    return output_soup.prettify()

def main():
    # Define input and output paths
    input_path = os.path.join('input', 'datasheet-list.html')
    output_dir = 'output'
    output_path = os.path.join(output_dir, 'datasheet-list.html')
    
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)
    
    try:
        # Read input file
        with open(input_path, 'r', encoding='utf-8') as file:
            input_html = file.read()
        
        # Convert the HTML
        output_html = convert_to_datasheet_format(input_html)
        
        # Write output file
        with open(output_path, 'w', encoding='utf-8') as file:
            file.write(output_html)
            
        print(f"Conversion successful! Output saved to: {output_path}")
        
    except FileNotFoundError:
        print(f"Error: Input file not found at {input_path}")
    except Exception as e:
        print(f"An error occurred: {str(e)}")

if __name__ == "__main__":
    main()