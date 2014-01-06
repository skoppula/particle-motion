abstract class physObject{
  
  float radius = 1;
  int counter = 0;
  int[] screenPos = new int[2];
  float[] pos = new float[2];
  float velocity = 0, mass = 0;
  //This is essentially tan(theta)
  float angle = 0;
  float vX = 0, vY = 0;
  String name;
  
  public physObject(){}
  
  float getX(){
    return pos[0];
  }
  
  float getY(){
    return pos[1];
  }
  
  float getVelX(){
    return vX;
  }
  float getVelY(){
    return vY;
  }
  
  void changeVelX(float v){
    vX = v;
  }
  
  void changeVelY(float v){
    vY = v;
  }

  public physObject(int x, int y, float initVel, float direction, boolean isRadian){
    pos[0] = x;
    pos[1] = y;
    velocity = initVel;
    angle = direction;
    if(isRadian){
      vX = velocity*cos(angle);
      vY = velocity*cos(angle);
    }
    else{
      vX = velocity*cos(angle*PI/180);
      vY = velocity*cos(angle*PI/180);       
    }
  }
  
  void updateStatus(){}
  
  boolean isOutBoundsY(){
    if(0<=screenPos[1]&&screenPos[1]<=sizeOfScreen[1])
      return false;
    else
      return true;
  }
  
  boolean isOutBoundsX(){
    if(0<=screenPos[0]&&screenPos[0]<=sizeOfScreen[0]){
      return false;
    }
    else
      return true;
  }
  
  void display(){}
  
  int getScreenX(){
    return screenPos[0];
  }
  
  int getScreenY(){
    return screenPos[1];
  }
  
  String toString(){
    return "Pixel position: (" + screenPos[0] + ", " + screenPos[1] + ") \n" + "Position: (" + pos[0] + ", " + pos[1] + ")";
  }
  
  float getRadius(){
    return radius;
  }
}
