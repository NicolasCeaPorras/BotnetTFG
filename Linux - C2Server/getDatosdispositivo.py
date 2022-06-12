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
docs = firestore_db.collection(u'datosDispositivo').where(u'Bot_ID', u'!=', "").stream()
cantidad = 0
for doc in docs:
    if(not(doc.get("Bot_ID") in lista)):
        lista.append(doc.get("Bot_ID"))

print(lista)

seleccionBot = input("Select a Bot ID or press enter to get the information for all available bots: ")
if(seleccionBot == ""):
    for i in lista:
        doc_ref = firestore_db.collection(u'datosDispositivo').document(i)
        doc = doc_ref.get()
        if doc.exists:
            print(f'Device data for {seleccionBot}:')
            texto = str(doc.to_dict())
            texto = texto[1:len(texto)-1]
            texto = texto.split(",")
            for j in texto:
                print(f"{j}")
        else:
            print(u'The selected Bot ID is wrong or it does not exist on the Database')
else:
    doc_ref = firestore_db.collection(u'datosDispositivo').document(seleccionBot)
    doc = doc_ref.get()
    if doc.exists:
        print(f'Device data for {seleccionBot}:')
        texto = str(doc.to_dict())
        texto = texto[1:len(texto)-1]
        texto = texto.split(",")
        for j in texto:
            print(f"{j}")
    else:
        print(u'The selected Bot ID is wrong or it does not exist on the Database')
print("\n")