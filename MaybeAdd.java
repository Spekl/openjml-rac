public class MaybeAdd {

  //@ requires 0 < a && a < 1000;
  //@ requires 0 < b && b < 1000;
  //@ ensures  0 < \result;
  public static int add(int a, int b){
      return a-b;
  }


  public static void main(String args[]){

      System.out.println(MaybeAdd.add(1,2));

  }

}
