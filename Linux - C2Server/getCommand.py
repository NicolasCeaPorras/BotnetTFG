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
docs = firestore_db.collection(u'comandoEjecutado').where(u'Bot_ID', u'!=', "").stream()
cantidad = 0
for doc in docs:
    if(not(doc.get("Bot_ID") in lista)):
        lista.append(doc.get("Bot_ID"))

print(lista)

seleccionBot = input("Select a Bot ID to get its commands output: ")

lista2 = []
docs = firestore_db.collection(u'comandoEjecutado').where(u'Bot_ID', u'==', seleccionBot).stream()
cantidad = 0
for doc in docs:
    if(not(doc.id in lista2)):
        lista2.append(doc.id)

print(lista2)

seleccionBot2 = input("Select a command to se its output: ")

doc_ref = firestore_db.collection(u'comandoEjecutado').document(seleccionBot2)
doc = doc_ref.get()
if doc.exists:
    print(f'Device data for {seleccionBot2}: {doc.to_dict()}')
else:
    print(u'The selected Bot ID is wrong or it does not exist on the Database')
