import requests
from bs4 import BeautifulSoup

def get_product_prices(product_name):
    query = product_name.replace(" ", "+")
    url = f"https://www.google.com/search?q={query}+prix+france"
    
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }
    
    response = requests.get(url, headers=headers)
    
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, "html.parser")
        
        # Extraction des prix (cela dépend du site, ici c'est un exemple)
        prices = soup.find_all("span", class_="a-price-whole")  # Classe à adapter selon les résultats
        if prices:
            print("Prix trouvés :")
            for price in prices[:5]:  # Afficher les 5 premiers prix
                print(price.text.strip())
        else:
            print("Aucun prix trouvé.")
    else:
        print("Erreur lors de la récupération des données.")

if __name__ == "__main__":
    product_name = input("Input the product name: ")
    get_product_prices(product_name)
