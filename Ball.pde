class Ball extends physObject{
  
  float materialC = 0.1;
  float dampC = airDens*materialC*0.5*4*sq(radius)*PI/2;
 
  
  public Ball(float x, float y, float initVel, float direction, boolean isRadian, float rad){
    name = "Ball";
    pos[0] = x;
    pos[1] = y;
    updateScreenPos();
    velocity = initVel;
    angle = direction;
    if(isRadian){
      vX = velocity*cos(angle);
      vY = velocity*sin(angle);
    }
    else{
      vX = velocity*cos(angle*PI/180);
      vY = velocity*sin(angle*PI/180);
    }
    radius = rad;
  }
  
  void display(){
    ellipse(screenPos[0], sizeOfScreen[1] - screenPos[1], round((radius)*sizeOfScreen[0]/sizeOfField[0]), round((radius)*sizeOfScreen[1]/sizeOfField[1]));
  }
  
  void updateStatus(){
    pos[0] += vX*timeDif;
    pos[1] += vY*timeDif;
    if(vY>0){
      vY += (accel)*timeDif;
    }
    else
      vY += (accel)*timeDif;
    if(vX>0)
      vX += 0;
    else
      vX += 0;
    updateVelocity();
    updateScreenPos();
  }
  
  void updateVelocity(){
    velocity = sqrt(sq(vX)+sq(vY));
  }
  
  void updateScreenPos(){
    screenPos[0] = round(pos[0]*sizeOfScreen[0]/sizeOfField[0]);
    screenPos[1] = round(pos[1]*sizeOfScreen[1]/sizeOfField[1]);
  }
 
}
