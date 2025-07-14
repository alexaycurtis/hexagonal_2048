public class Tile {
    public int value;

    public Tile(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int newValue){
        value = newValue;
    }

    public boolean isEmpty(){
        return value == 0;
    }

    @Override
    public String toString(){
        return value == 0 ? "." : String.valueOf(value);
    }
}
