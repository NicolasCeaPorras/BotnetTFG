import os
import firebase_admin
from firebase_admin import credentials, firestore# initialize sdk
from datetime import datetime

cred = credentials.Certificate("tfg-nicolas-cea-firebase-adminsdk-bq588-0a7759e31d.json")

firebase_admin.initialize_app(cred)# initialize firestore instance

firestore_db = firestore.client()# add data

now = datetime.now()
dt_string = now.strftime("%d-%m-%Y %H:%M:%S")

os.system("python3 botsVivos.py")
seleccionBot = input("Select a Bot ID or press enter to get the name + number for all available bots contact list: ")
if(seleccionBot == ""):
    botList = []
    with open('availableBots.txt', 'r') as file:
        data = file.read().rstrip()
        botList = data.split(";")
    for i in botList:
        doc_ref = firestore_db.collection(u'contactos').document(i)
        doc = doc_ref.get()
        if doc.exists:
            print(f'Contacts for {i}: {doc.to_dict()}')
        else:
            print(u'The selected Bot ID is wrong or it does not exist on the Database')
else:
    doc_ref = firestore_db.collection(u'contactos').document(seleccionBot)
    doc = doc_ref.get()
    if doc.exists:
        print(f'Contacts for {seleccionBot}: {doc.to_dict()}')
    else:
        print(u'The selected Bot ID is wrong or it does not exist on the Database')
