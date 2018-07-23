package model;

import controller.Controller;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ISiteNoticia extends Remote {

    boolean getParcial(int idNoticia) throws RemoteException;
    void noticiaFinal(int idNoticia, boolean resultadoFinal) throws RemoteException;
    void consenso(int idNoticia, List<ISiteNoticia> servidores) throws RemoteException;
}
