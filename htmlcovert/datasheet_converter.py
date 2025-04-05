# datasheet_converter.py
from bs4 import BeautifulSoup
from html_utils import read_html_file, write_html_file, init_soup

def convert_datasheet(html_content):
    """Convert datasheet HTML structure"""
    soup = init_soup(html_content)
    
    # Create new container div
    new_container = soup.new_tag('div')
    new_container['class'] = 'product-datasheet'
    
    # Create the new table
    new_table = soup.new_tag('table')
    new_table['class'] = 'datasheet-list'
    
    # Get all existing tbody sections
    tbodies = soup.find_all('tbody')
    
    # Skip the first tbody (Types de produit) and process the rest
    for tbody in tbodies[1:]:
        new_tbody = process_tbody(soup, tbody)
        if new_tbody:
            new_table.append(new_tbody)
    
    # Add toggle button and gradient
    add_toggle_elements(soup, new_table)
    
    new_container.append(new_table)
    return new_container.prettify()

def process_tbody(soup, tbody):
    """Process individual tbody section"""
    new_tbody = soup.new_tag('tbody')
    
    rows = tbody.find_all('tr')
    for row in rows:
        new_row = process_row(soup, row)
        if new_row:
            new_tbody.append(new_row)
    
    return new_tbody

def process_row(soup, row):
    """Process individual row"""
    new_row = soup.new_tag('tr')
    new_row['class'] = row.get('class', [])
    
    if row.find('th'):
        # Handle header row
        th = row.find('th')
        new_th = soup.new_tag('th')
        new_th['colspan'] = '2'
        new_th.string = th.get_text(strip=True)
        new_row.append(new_th)
    else:
        # Handle data row
        key_cell = row.find('td', class_='datasheet-listItemKey')
        value_cell = row.find('td', class_='datasheet-listItemValue')
        
        if key_cell and value_cell:
            new_key = soup.new_tag('td')
            new_key['class'] = 'datasheet-listItemKey'
            new_key.string = key_cell.get_text(strip=True)
            
            new_value = soup.new_tag('td')
            new_value['class'] = 'datasheet-listItemValue'
            new_value.string = value_cell.get_text(strip=True)
            
            new_row.append(new_key)
            new_row.append(new_value)
    
    return new_row

def add_toggle_elements(soup, table):
    """Add toggle button and gradient elements"""
    tfoot = soup.new_tag('tfoot')
    row = soup.new_tag('tr')
    cell = soup.new_tag('td')
    cell['colspan'] = '2'
    
    gradient = soup.new_tag('div')
    gradient['class'] = 'datasheet-gradient'
    
    button = soup.new_tag('button')
    button['class'] = 'datasheet-toggle-btn'
    button['type'] = 'button'
    
    show_more = soup.new_tag('span')
    show_more['class'] = 'show-more'
    show_more.string = 'Afficher tous les détails'
    
    show_less = soup.new_tag('span')
    show_less['class'] = 'show-less'
    show_less.string = 'Afficher moins de détails'
    
    button.append(show_more)
    button.append(show_less)
    cell.append(gradient)
    cell.append(button)
    row.append(cell)
    tfoot.append(row)
    table.append(tfoot)

if __name__ == "__main__":
    # Read input HTML
    html_content = read_html_file('datasheet.html')
    if html_content:
        # Convert HTML
        converted_html = convert_datasheet(html_content)
        # Write output
        write_html_file(converted_html, 'datasheet.html')