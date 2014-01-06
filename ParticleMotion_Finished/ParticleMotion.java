import processing.core.*; 
import processing.xml.*; 

import java.io.*; 
import java.util.*; 
import javax.swing.*; 
import java.awt.*; 
import java.applet.*; 
import javax.sound.sampled.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class ParticleMotion extends PApplet {

/*
Physics Engine
  Kinematics Version 8
  Sound
  GUI Simplified
  Dampening
Skanda Koppula
May 2011
*/








static int[] sizeOfScreen = {800, 600};
float[] sizeOfField = {100, 100};
float accel = -9.8f;
float timeDif = 0.005f;
float distance = 0, distance1 = 0;
ArrayList<physObject> listObjects = new ArrayList<physObject>();
Mutator m = new Mutator();;
boolean pause = false;
boolean notShowPath = true;
boolean collision = true;
boolean textShow = true;
float damp = 0.95f;
float airDens = 1;

long timeEla;
int[] newSPos = new int[4];

boolean auStart = false;
boolean music = true;
File bgMusicFile = new File("BgMusic.wav");
File dingFile = new File("ding.wav");
Clip clip = null;

public void startBgMusic() {
  if ((! auStart) && (clip != null)) {
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }
}

public void stopBgMusic() {
  if (auStart && (clip != null)) {
    clip.stop();
  }
}

public Clip setupClip(File clipFile) {
  Clip clip = null;

  try {
    AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
    AudioFormat format = audioIn.getFormat();

    /**
     * we can't yet open the device for ALAW/ULAW playback,
     * convert ALAW/ULAW to PCM
     */
    if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
      (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
      AudioFormat tmp = new AudioFormat(
      AudioFormat.Encoding.PCM_SIGNED, 
      format.getSampleRate(), 
      format.getSampleSizeInBits() * 2, 
      format.getChannels(), 
      format.getFrameSize() * 2, 
      format.getFrameRate(), 
      true);
      audioIn = AudioSystem.getAudioInputStream(tmp, audioIn);
      format = tmp;
    }
    DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat(), 
    ((int) audioIn.getFrameLength() * format.getFrameSize()));

    clip = (Clip) AudioSystem.getLine(info);
    clip.addLineListener(new LineListener() {
      public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.START) {
          auStart = true;
        } 
        else if (event.getType() == LineEvent.Type.STOP) {
          // event.getLine().close();
          auStart = false;
        } 
        //            else {
        //              print ("Encountered error in background music status!\n");
        //            }
      }
    }
    );

    clip.open(audioIn);
  } 
  catch (UnsupportedAudioFileException e) {
    e.printStackTrace();
  } 
  catch (IOException e) {
    e.printStackTrace();
  } 
  catch (LineUnavailableException e) {
    e.printStackTrace();
  }

  return clip;
}

public void makeDing() {
  try {
    AudioInputStream audioIn = AudioSystem.getAudioInputStream(dingFile);

    AudioFormat format = audioIn.getFormat();

    /**
     * we can't yet open the device for ALAW/ULAW playback,
     * convert ALAW/ULAW to PCM
     */
    if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
      (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
      AudioFormat tmp = new AudioFormat(
      AudioFormat.Encoding.PCM_SIGNED, 
      format.getSampleRate(), 
      format.getSampleSizeInBits() * 2, 
      format.getChannels(), 
      format.getFrameSize() * 2, 
      format.getFrameRate(), 
      true);
      audioIn = AudioSystem.getAudioInputStream(tmp, audioIn);
      format = tmp;
    }
    DataLine.Info info = new DataLine.Info(
    Clip.class, 
    audioIn.getFormat(), 
    ((int) audioIn.getFrameLength() *
      format.getFrameSize()));

    Clip clip = (Clip) AudioSystem.getLine(info);

    clip.addLineListener(new LineListener() {
      public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
          // event.getLine().close();
        }
      }
    }
    );

    try {
      clip.open(audioIn);
      clip.start();
      clip.drain();
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  } 
  catch (UnsupportedAudioFileException e) {
    e.printStackTrace();
  } 
  catch (IOException e) {
    e.printStackTrace();
  } 
  catch (LineUnavailableException e) {
    e.printStackTrace();
  }
}

public void setup(){
  clip = setupClip(bgMusicFile);
  Intro.displayIntro();
  size(sizeOfScreen[0], sizeOfScreen[1]);
  resetMap();
  fill(color(0,255,0));
}
int counter = 0;
public void draw(){
  if (listObjects.size() > 0&&music) {
    startBgMusic();
  } 
  else if(!music){
    stopBgMusic();
  }
  if(keyPressed&&(key=='n'||key=='a'||key=='f'||key=='t'||key=='p'||key=='h'||key=='r'||key=='l'||key=='c'||key=='e'||key=='m'||key=='d')){
    m.act(key);
  }
  keyPressed = false;
  counter++;
  if(pause){
    delay(100);
    text("Acceleration: " + accel,50,50);
    text("Show Path: " + notShowPath,50,65);
    text("Do Collisions: " + collision,50,80);
    text("Pause: " + pause, 50, 95);
  }
  else{
    delay(round(1000*timeDif));
    updateObjStatus();
    updateDisMap();
    if(collision){
      for(int index = 0; index < listObjects.size(); index++){
        for(int index2 = index+1; index2 < listObjects.size(); index2++){
          distance = distance(listObjects.get(index), listObjects.get(index2));
          if(distance<(listObjects.get(index).getRadius()+listObjects.get(index2).getRadius()-0.1f)&&distance!=0){
            reverseDirection(listObjects.get(index), listObjects.get(index2));
          }
        }
    }
    }
  }
  if(notShowPath&&counter%5==4){
    clearMap();
    updateDisMap();
  }
  if(textShow){
    loadPixels();
    for(int index1 = width*35+50; index1 < width*100+50; index1++){
      if(index1%width>50&&index1%width<200)
        pixels[index1] = color(0, 0, 0);  
    }
    updatePixels();
    text("Acceleration: " + accel,50,50);
    text("Show Path: " + !notShowPath,50,65);
    text("Do Collisions: " + collision,50,80);
    text("Pause: " + pause, 50, 95);
  }
}

public void reverseDirection(physObject o1, physObject o2){
   float tempX = o1.getVelX();
   float tempY = o1.getVelY();
   
   makeDing();
   o1.changeVelX(damp*o2.getVelX());
   o1.changeVelY(damp*o2.getVelY());
   o2.changeVelX(damp*tempX);
   o2.changeVelY(damp*tempY);
}

public float distance(physObject b1, physObject b2){
  distance1 = sqrt(sq(b1.getX()-b2.getX())+sq(b1.getY()-b2.getY()));
  return distance1;
}

public void updateObjStatus(){
  for(int index = 0; index < listObjects.size(); index++){
    physObject o1 = listObjects.get(index);
    o1.updateStatus();
    if(o1.isOutBoundsX()||o1.isOutBoundsY()){
      if(collision==false){
        listObjects.remove(index);
      }
      else if(o1.isOutBoundsX()){
        o1.changeVelX(-damp*o1.getVelX());
        makeDing();
      }
      else{
        o1.changeVelY(-damp*o1.getVelY());
        makeDing();
      }
    }
  }
}

public void updateDisMap(){
  for(int index = 0; index < listObjects.size(); index++){
    listObjects.get(index).display();
  }
}

public void resetMap(){
  loadPixels();
  for(int index1 = 0; index1 < sizeOfScreen[0]*sizeOfScreen[1]; index1++){
    try{
      pixels[index1] = color(0, 0, 0);
    }
    catch(Exception e){
      print("Oh no!");
    }
  }
  for(int index = 0; index < listObjects.size(); index++)
    listObjects.remove(index);
  updatePixels();
}

public void clearMap(){
  loadPixels();
  for(int index1 = 0; index1 < sizeOfScreen[0]*sizeOfScreen[1]; index1++){
    pixels[index1] = color(0, 0, 0);  
  }
  updatePixels();
}

public void mousePressed(){
    newSPos[0] = mouseX;
    newSPos[1] = mouseY;
    timeEla = System.nanoTime();
}
public void mouseReleased(){
  timeEla = System.nanoTime() - timeEla;
  m.newPhysObjectM(newSPos[0], newSPos[1], pmouseX, pmouseY, timeEla);
}
class Ball extends physObject{
  
  float materialC = 0.1f;
  float dampC = airDens*materialC*0.5f*4*sq(radius)*PI/2;
 
  
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
  
  public void display(){
    ellipse(screenPos[0], sizeOfScreen[1] - screenPos[1], round((radius)*sizeOfScreen[0]/sizeOfField[0]), round((radius)*sizeOfScreen[1]/sizeOfField[1]));
  }
  
  public void updateStatus(){
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
  
  public void updateVelocity(){
    velocity = sqrt(sq(vX)+sq(vY));
  }
  
  public void updateScreenPos(){
    screenPos[0] = round(pos[0]*sizeOfScreen[0]/sizeOfField[0]);
    screenPos[1] = round(pos[1]*sizeOfScreen[1]/sizeOfField[1]);
  }
 
}
static class Intro{
  
  public Intro(){}
  
  public static int displayIntro(){
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
  
  public static void displayInst(){
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
class Mutator{
  
  boolean rad;
  float vel, r, a;
  int x, y;
  
  public Mutator(){}
  
  public void act(char c){
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
  
  public int newPhysObject(){
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
  
  public void newPhysObjectM(int xS1, int yS1, int xS2, int yS2, long time){
    float x1 = (xS1+0.0f)/sizeOfScreen[0]*sizeOfField[0];
    float y1 = (height-yS1+0.0f)/sizeOfScreen[1]*sizeOfField[1];
    float x2 = (xS2+0.0f)/sizeOfScreen[0]*sizeOfField[0];
    float y2 = (height-yS2+0.0f)/sizeOfScreen[1]*sizeOfField[1];
    float velocity = sqrt(sq(x2-x1)+sq(y2-y1))/time*pow(10,9);
    float angle;
    if((x2-x1)<0)
      angle = PI+atan((y2-y1)/(x2-x1));
    else
      angle = atan((y2-y1)/(x2-x1));
    listObjects.add(new Ball(x1, y1, velocity, angle, true, 1));
  }
  
  public void changeAccel(){
    try{
      float a =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new field acceleration", "Acceleration", 1));  
      accel = a;
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input", "Invalid!", JOptionPane.WARNING_MESSAGE);
    }
  }
  
  public void changeDamp(){
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
  
  public void changeScreen(){
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
  
  public void changeField(){
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
  
  public void changeTime(){
    try{
      float t =  Float.parseFloat(JOptionPane.showInputDialog(null, "Enter a new time difference", "Time difference", 1));  
      timeDif = t;
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, "Sorry, that's not valid input", "Incorrect!", JOptionPane.WARNING_MESSAGE);
    }
  }
}
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
  
  public float getX(){
    return pos[0];
  }
  
  public float getY(){
    return pos[1];
  }
  
  public float getVelX(){
    return vX;
  }
  public float getVelY(){
    return vY;
  }
  
  public void changeVelX(float v){
    vX = v;
  }
  
  public void changeVelY(float v){
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
  
  public void updateStatus(){}
  
  public boolean isOutBoundsY(){
    if(0<=screenPos[1]&&screenPos[1]<=sizeOfScreen[1])
      return false;
    else
      return true;
  }
  
  public boolean isOutBoundsX(){
    if(0<=screenPos[0]&&screenPos[0]<=sizeOfScreen[0]){
      return false;
    }
    else
      return true;
  }
  
  public void display(){}
  
  public int getScreenX(){
    return screenPos[0];
  }
  
  public int getScreenY(){
    return screenPos[1];
  }
  
  public String toString(){
    return "Pixel position: (" + screenPos[0] + ", " + screenPos[1] + ") \n" + "Position: (" + pos[0] + ", " + pos[1] + ")";
  }
  
  public float getRadius(){
    return radius;
  }
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "ParticleMotion" });
  }
}
