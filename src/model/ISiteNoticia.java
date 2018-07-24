package model;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ISiteNoticia extends Remote
{

    boolean getAvaliacao(int idNoticia) throws RemoteException;
    void definirAvaliacao(int idNoticia, boolean resultadoFinal) throws RemoteException;
    void consenso(int idNoticia, List<ISiteNoticia> servidores) throws RemoteException;
}
