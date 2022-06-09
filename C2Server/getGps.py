import os
import firebase_admin
from firebase_admin import credentials, firestore# initialize sdk
from datetime import datetime

cred = credentials.Certificate("tfg-nicolas-cea-firebase-adminsdk-bq588-0a7759e31d.json")

firebase_admin.initialize_app(cred)# initialize firestore instance

firestore_db = firestore.client()# add data

now = datetime.now()
dt_string = now.strftime("%d-%m-%Y %H:%M:%S")
coordx = "0"
coordy = "0"

lista = []
docs = firestore_db.collection(u'gps').where(u'Bot_ID', u'!=', "").stream()
cantidad = 0
for doc in docs:
    if(not(doc.get("Bot_ID") in lista)):
        lista.append(doc.get("Bot_ID"))

print(lista)

seleccionBot = input("Select a Bot ID to get all its known locations: ")

lista2 = []
docs = firestore_db.collection(u'gps').where(u'Bot_ID', u'==', seleccionBot).stream()
cantidad = 0
for doc in docs:
    if(not(doc.id in lista2)):
        lista2.append(doc.id)

print(lista2)

seleccionBot2 = input("Select a date to know the exact location: ")

doc_ref = firestore_db.collection(u'gps').document(seleccionBot2)
doc = doc_ref.get()
if doc.exists:
    print(doc.to_dict())
    coordx = doc.to_dict()["GPS"]["latitude"]
    coordy = doc.to_dict()["GPS"]["longitude"]
    print(f'Location for {seleccionBot2}:')
    texto = str(doc.to_dict())
    texto = texto[1:len(texto)-1]
    texto = texto.split(",")
    for j in texto:
        print(f"{j}")
else:
    print(u'The selected Bot ID is wrong or it does not exist on the Database')

print(f"To locate bot on a map enter the following link: https://maps.google.com/?q={coordx},{coordy}")

print("\n")