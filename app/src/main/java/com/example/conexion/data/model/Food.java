package com.example.conexion.data.model;
public class Food {
    private long id;
    private String nombre;
    private int calorias;

    public Food(long id, String nombre, int calorias) {
        this.id = id;
        this.nombre = nombre;
        this.calorias = calorias;
    }

        public long getId() {
            return id;
        }
        public String getNombre(){
        return nombre;
    }
    public int getCalorias(){
        return calorias;
    }

    private void setId(long id) {
        this.id = id;
    }
        public void setNombre(String nombre){
            this.nombre = nombre;
        }
        public void setCalorias(int calorias){
        this.calorias = calorias;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
        " calorias= " + calorias +
                '}';
    }
}
