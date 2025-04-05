from icalendar import Calendar, Event
from datetime import datetime, timedelta
import pytz
import pandas as pd

# Données complètes de l'emploi du temps
data = [
    {"Date": "2025-01-08", "Projet": "Définir les technologies", "Heures": 6},
    {"Date": "2025-01-09", "Projet": "Définir les technologies", "Heures": 6},
    {"Date": "2025-01-10", "Projet": "Définir les technologies", "Heures": 6},
    {"Date": "2025-01-13", "Projet": "Définir les technologies", "Heures": 2},
    {"Date": "2025-01-13", "Projet": "Tester une application full-stack", "Heures": 4},
    {"Date": "2025-01-14", "Projet": "Tester une application full-stack", "Heures": 6},
    {"Date": "2025-01-15", "Projet": "Tester une application full-stack", "Heures": 6},
    {"Date": "2025-01-16", "Projet": "Tester une application full-stack", "Heures": 6},
    {"Date": "2025-01-17", "Projet": "Tester une application full-stack", "Heures": 6},
    {"Date": "2025-01-20", "Projet": "Tester une application full-stack", "Heures": 2},
    {"Date": "2025-01-20", "Projet": "Développer une application full-stack complète", "Heures": 4},
    {"Date": "2025-01-21", "Projet": "Développer une application full-stack complète", "Heures": 6},
    {"Date": "2025-01-22", "Projet": "Développer une application full-stack complète", "Heures": 6},
    {"Date": "2025-01-23", "Projet": "Développer une application full-stack complète", "Heures": 6},
    {"Date": "2025-01-24", "Projet": "Développer une application full-stack complète", "Heures": 6},
    {"Date": "2025-01-27", "Projet": "Développer une application full-stack complète", "Heures": 4},
    {"Date": "2025-01-27", "Projet": "Point d'étape", "Heures": 2},
    {"Date": "2025-01-28", "Projet": "Cahier des charges", "Heures": 6},
    {"Date": "2025-01-29", "Projet": "Cahier des charges", "Heures": 6},
    {"Date": "2025-01-30", "Projet": "Cahier des charges", "Heures": 6},
    {"Date": "2025-01-31", "Projet": "Cahier des charges", "Heures": 6},
    {"Date": "2025-02-03", "Projet": "Validation, chiffrage", "Heures": 6},
    {"Date": "2025-02-04", "Projet": "Validation, chiffrage", "Heures": 6},
    {"Date": "2025-02-05", "Projet": "Gestion CI/CD", "Heures": 6},
    {"Date": "2025-02-06", "Projet": "Gestion CI/CD", "Heures": 6},
    {"Date": "2025-02-07", "Projet": "Gestion CI/CD", "Heures": 6},
    {"Date": "2025-02-10", "Projet": "Gestion CI/CD", "Heures": 6},
    {"Date": "2025-02-11", "Projet": "Encadrer une équipe", "Heures": 6},
    {"Date": "2025-02-12", "Projet": "Encadrer une équipe", "Heures": 6},
    {"Date": "2025-02-13", "Projet": "Encadrer une équipe", "Heures": 6},
    {"Date": "2025-02-14", "Projet": "Planifier les tests", "Heures": 6},
    {"Date": "2025-02-17", "Projet": "Planifier les tests", "Heures": 6},
    {"Date": "2025-02-18", "Projet": "Planifier les tests", "Heures": 6},
    {"Date": "2025-02-19", "Projet": "Solution fonctionnelle", "Heures": 6},
    {"Date": "2025-02-20", "Projet": "Solution fonctionnelle", "Heures": 6},
    {"Date": "2025-02-21", "Projet": "Solution fonctionnelle", "Heures": 6},
]


# Créer un DataFrame
schedule_df = pd.DataFrame(data)

# Créer un calendrier iCalendar
cal = Calendar()

# Définir le fuseau horaire
timezone = pytz.timezone("Europe/Paris")

# Ajouter les événements au calendrier
for _, row in schedule_df.iterrows():
    event = Event()
    event.add('summary', row['Projet'])
    start_time = timezone.localize(datetime.strptime(row['Date'], '%Y-%m-%d') + timedelta(hours=9))  # Commence à 9h
    end_time = start_time + timedelta(hours=row['Heures'])
    event.add('dtstart', start_time)
    event.add('dtend', end_time)
    event.add('description', f"{row['Heures']} heures prévues pour ce projet.")
    cal.add_component(event)

# Sauvegarder le fichier iCalendar
ical_path = "emploi_du_temps_fullstack.ics"
with open(ical_path, 'wb') as f:
    f.write(cal.to_ical())

print(f"Le fichier iCalendar a été généré : {ical_path}")
