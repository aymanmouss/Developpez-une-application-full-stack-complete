# pros_cons_converter.py
from bs4 import BeautifulSoup
from html_utils import read_html_file, write_html_file, init_soup

def convert_pros_cons(html_content):
    """Convert pros and cons HTML structure"""
    soup = init_soup(html_content)
    
    # Create the new container
    new_container = soup.new_tag('div')
    new_container['class'] = 'pros-cons-container'
    
    # Add title
    title = create_title(soup)
    new_container.append(title)
    
    # Add description
    # description = create_description(soup)
    # new_container.append(description)
    
    # Create flex container for pros and cons
    flex_container = create_flex_container(soup)
    
    # Find original pros and cons
    original_content = soup.find('div', class_='pros-cons')
    if original_content:
        # Process pros
        pros_box = original_content.find('div', class_='box')
        if pros_box:
            pros_card = create_card(soup, 'pros', pros_box)
            flex_container.append(pros_card)
        
        # Process cons
        cons_box = pros_box.find_next_sibling('div', class_='box') if pros_box else None
        if cons_box:
            cons_card = create_card(soup, 'cons', cons_box)
            flex_container.append(cons_card)
    
    new_container.append(flex_container)
    return new_container.prettify()

def create_title(soup):
    """Create title element"""
    title = soup.new_tag('h2')
    title['class'] = 'pros-cons-title'
    title.string = 'Points forts et points faibles'
    return title

# def create_description(soup):
#     """Create description element"""
#     desc = soup.new_tag('p')
#     desc['class'] = 'pros-cons-description'
#     desc.string = "Notre équipe Unnov vous présente son analyse complète"
#     return desc

def create_flex_container(soup):
    """Create flex container for cards"""
    container = soup.new_tag('div')
    container['class'] = 'pros-cons-flex'
    return container

def create_card(soup, card_type, original_box):
    """Create a pros or cons card"""
    card = soup.new_tag('div')
    card['class'] = f'{card_type}-card'
    
    header = soup.new_tag('h3')
    header['class'] = 'card-header'
    header.string = 'Points forts' if card_type == 'pros' else 'Points faibles'
    card.append(header)
    
    list_elem = soup.new_tag('ul')
    list_elem['class'] = f'{card_type}-list'
    
    items = original_box.find_all('li')
    for item in items:
        li = soup.new_tag('li')
        li.string = item.get_text(strip=True)
        list_elem.append(li)
    
    card.append(list_elem)
    return card

if __name__ == "__main__":
    # Read input HTML
    html_content = read_html_file('pros_cons.html')
    if html_content:
        # Convert HTML
        converted_html = convert_pros_cons(html_content)
        # Write output
        write_html_file(converted_html, 'pros_cons.html')