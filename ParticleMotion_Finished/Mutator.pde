class Mutator{
  
  boolean rad;
  float vel, r, a;
  int x, y;
  
  public Mutator(){}
  
  void act(char c){
    if(key=='n')
      newPhysObject();
    else if(key=='a')
      changeAccel();
    else if(key=='m')
      music = !music;
    else if(key=='s')
      changeScreen();
    else if(key=='f')
      changeField();
    else if(key=='t')
      changeTime();
    else if(key=='p')
      pause = !pause;
    else if(key=='l')
      notShowPath = !notShowPath;
    else if(key=='h')
      Intro.displayInst();
    else if(key=='r')
      resetMap();
    else if(key=='d')
      changeDamp();
    else if(key=='e'){
      textShow = !textShow;
      if(textShow==false)
        clearMap();
    }
    else if(key=='c')
      collision=!collision;
  }
  
  int newPhysObject(){
    String inPos = JOptionPane.showInputDialog(null, "Enter X and Y initial position. Example: 50 75", "Create New Particle: Initial Position", 1);
    String velR =  JOptionPane.showInputDialog(null, "Enter velocity and radius. Example: 20 5", "Create New Particle: Velocity and Radius", 1);
    String radOrDegA =  JOptionPane.showInputDialog(null, "Enter if its in degree and the angle measure initial position. Example: Yes 63", "Create New Particle: Initial Position", 1);
    if(inPos==null||velR==null||radOrDegA==null)
      return 0;
    try{
      if("".equals(inPos)||"".equals(radOrDegA)||"".equals(velR)){
        throw new Exception();
      }
      String[] items = inPos.split(" ");
      x = Integer.parseInt(items[0]);
      y = Integer.parseInt(items[1]);
      items = velR.split(" ");
      vel = Float.parseFloat(items[0]);
      r = Float.parseFloat(items[1]);
      items = radOrDegA.split(" ");
      if(items[0].equals("Yes"))
        rad = false;
      else
        rad = true;
      float a = Float.parseFloat(items[1]);
      
    }
    catch(Exception e){
      
      JOptionPane.showMessageDialog(null, "I'm sorry. I was not able to understand you're input. Please try again", "Invalid: Try Again", JOptionPane.WARNING_MESSAGE);
      this.newPhysObject();
    }
        
    listObjects.add(new Ball(x, y, vel, a, rad, r));
    return 0;
  }
  
  void newPhysObjectM(int xS1, int yS1, int xS2, int yS2, long time){
    float x1 = (xS1+0.0)/sizeOfScreen[0]*sizeOfField[0];
    float y1 = (height-yS1+0.0)/sizeOfScreen[1]*sizeOfField[1];
    float x2 = (xS2+0.0)/sizeOfScreen[0]*sizeOfField[0];
    float y2 = (height-yS2+0.0)/sizeOfScreen[1]*sizeOfField[1];
    float velocity = sqrt(sq(x2-x1)+sq(y2-y1))/time*pow(10,9);
    float angle;
    if((x2-x1)<0)
      angle = PI+atan((y2-y1)/(x2-x1));
    else
      angle = atan((y2-y1)/(x2-x1));
    listObjects.add(new Ball(x1, y1, velocity, angle, true, 1));
  }
  
  void changeAccel(){
    try{
      float a =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new field acceleration", "Acceleration", 1));  
      accel = a;
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input", "Invalid!", JOptionPane.WARNING_MESSAGE);
    }
  }
  
  void changeDamp(){
    try{
      float a =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new dampening constant. Remember, 1 means no dampening.\n" + 
      "For sanity sake, choose number >0 and <=1", "Dampening", 1));
      if(a<=0||a>1)
        throw new Exception();
      damp = a;
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input", "Invalid!", JOptionPane.WARNING_MESSAGE);
    }
  }
  
  void changeScreen(){
    try{
      int x =  Integer.parseInt(JOptionPane.showInputDialog(null, "Enter a new screen (x - pixel) length", "Change Screen Size", 1));
      int y =  Integer.parseInt(JOptionPane.showInputDialog(null, "Enter a new screen (y - pixel) height", "Change Screen Size", 1));
      sizeOfScreen[0] = x;
      sizeOfScreen[1] = y;
      size(sizeOfScreen[0], sizeOfScreen[1]);
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input. The program will exit and you must restart it because you are such a fail.", "Incorrect!", JOptionPane.WARNING_MESSAGE);
      System.exit(0);
    }
  }
  
  void changeField(){
    try{
      float x =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new field length", "Change Screen Size", 1));
      float y =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new field height", "Change Screen Size", 1));  
      sizeOfField[0] = x;
      sizeOfField[1] = y;
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input. The program will exit and you must restart it because you are such a fail.", "Incorrect!", JOptionPane.WARNING_MESSAGE);
      System.exit(0);
    }
  }
  
  void changeTime(){
    try{
      float t =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new time difference", "Time difference", 1));  
      timeDif = t;
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input", "Incorrect!", JOptionPane.WARNING_MESSAGE);
    }
  }
}
