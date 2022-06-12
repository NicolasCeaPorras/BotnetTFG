import os
import firebase_admin
from firebase_admin import credentials, firestore# initialize sdk
from datetime import datetime

cred = credentials.Certificate("tfg-nicolas-cea-firebase-adminsdk-bq588-0a7759e31d.json")

firebase_admin.initialize_app(cred)# initialize firestore instance

firestore_db = firestore.client()# add data

now = datetime.now()
dt_string = now.strftime("%d-%m-%Y %H:%M:%S")

lista = []
docs = firestore_db.collection(u'notasUsuario').where(u'Bot_ID', u'!=', "").stream()
cantidad = 0
for doc in docs:
    if(not(doc.get("Bot_ID") in lista)):
        lista.append(doc.get("Bot_ID"))

print(lista)

seleccionBot = input("Select a Bot ID or press enter to get the information for all available bots: ")

lista2 = []
docs = firestore_db.collection(u'notasUsuario').where(u'Bot_ID', u'==', seleccionBot).stream()
cantidad = 0
for doc in docs:
    if(not(doc.id in lista2)):
        lista2.append(doc.id)

print(lista2)

seleccionBot2 = input("Select a day to see its note: ")

if(seleccionBot2 == ""):
    for i in lista2:
        doc_ref = firestore_db.collection(u'notasUsuario').document(i)
        doc = doc_ref.get()
        if doc.exists:
            print(f'Device data for {i}: {doc.to_dict()}')
        else:
            print(u'The selected Bot ID is wrong or it does not exist on the Database')
else:
    doc_ref = firestore_db.collection(u'notasUsuario').document(seleccionBot2)
    doc = doc_ref.get()
    if doc.exists:
        print(f'Device data for {seleccionBot}: {doc.to_dict()}')
    else:
        print(u'The selected Bot ID is wrong or it does not exist on the Database')

print("\n")
