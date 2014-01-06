static class Intro{
  
  public Intro(){}
  
  static int displayIntro(){
    String input = JOptionPane.showInputDialog(null, "Welcome to the Particle Motion Model!  This tool can be used to see the path of a particle(s)!" + 
    "Read these instructions carefully.\n\n" + 
    "MOST IMPORTANTLY - You can read and access the user instructions at any time by pressing h.Everything is in meters and seconds\n\n" + 
    "Below you must enter the size of particle field on your computer screen!\nEnter a two pixel values both from 100 to 2000, seperating the width and height by a space.\n" +
    "For example, a valid input would be \"800 600\" without the quotes. Leaving the field blank defaults to 800 by 600 pixels.\n\n", "Particle Motion Start", 1);
    
    if(input==null)
      System.exit(0);
    if("".equals(input)){
      sizeOfScreen[0] = 600;
      sizeOfScreen[1] = 600;
      return 0;
    }
    try{
      String[] items = input.split(" ");
      int xPos = Integer.parseInt(items[0]);
      int yPos = Integer.parseInt(items[1]);
      if(xPos<=2000&&xPos>100&&yPos<=2000&&yPos>100){
        sizeOfScreen[0] = xPos;
        sizeOfScreen[1] = xPos;
      }
      else
        throw new Exception();
    }
    catch(Exception e){
      
      JOptionPane.showMessageDialog(null, "I'm sorry. I was not able to understand you're input. Please try again", "Invalid: Try Again", JOptionPane.WARNING_MESSAGE);
      Intro.displayIntro();
    }
    return 0;
  }
  
  static void displayInst(){
    JOptionPane.showMessageDialog(null,
    "You may launch a particle by clicking on the screen, dragging your mouse, and releasing at another point OR clicking 'n' and entering initial particle values\n\n" +   
    "Click a to change the acceleration value\n\n" + 
    "Click f to change the dimensions of the field size that you are modeling.\n\n" + 
    "Click l to toggle visibility of the previous object path\n\n" +  
    "Click c to toggle collisions. Note that turning it off will cause the screen to be unbounded and objects to exit the screen.\n\n" + 
    "Click p to toggle pause\n\n" +
    "Click d to change the collision dampening constant" +
    "Click e to toggle whether the text shows or not", "Instructions Help", JOptionPane.WARNING_MESSAGE);
  }
}
