import java.util.Comparator;

class Libro{

    String libro;
    double ocurrencias;

    public Libro(String libro, double ocurrencias){
        this.libro = libro;
        this.ocurrencias = ocurrencias;
    }

    public String toString(){
        return this.libro + " " + this.ocurrencias;
    }

}

class SortByOcurrencias implements Comparator<Libro>{

    public int compare(Libro a, Libro b){
        return Double.compare(a.ocurrencias, b.ocurrencias);
    }

}