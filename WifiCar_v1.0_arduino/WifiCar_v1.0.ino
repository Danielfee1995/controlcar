#include <SoftwareSerial.h>

int led_pin = 7;
//定义一个十字节的字符型数组变量ch
char ch[10];
SoftwareSerial mySerial(10,11); // RX,TX

//获取串口缓冲区里的数据
int SerialData(char *buf){
  String data="";
  if(mySerial.available()>0){
    unsigned long start;
    start = millis();
    char c0 = mySerial.read();
    if(c0 == '+'){
      while(millis()-start<100){
        if(mySerial.available()>0){
          char c = mySerial.read();
          data+=c;
        }
        if(data.indexOf("\nOK") != -1){
          break;
        }
      }
      int sLen = strlen(data.c_str());
      int i,j;
      for(i = 0;i<=sLen;i++){
        if(data[i] == ':'){
          break;
        }
      }
      //判断是否存在id
      boolean found = false;
      for(j = 4;j<=i;j++){
        if(data[j] == ','){
          found = true;
          break;
        }
      }

      int iSize;
      if(found == true){
        //+IPD,<id>,<len>:<data>
        String _size = data.substring(j+1,i);
        iSize = _size.toInt();
        String str = data.substring(i+1,i+1+iSize);
        strcpy(buf,str.c_str());
      }else{
        //+IPD,<len>:<data>
        String _size = data.substring(4,i);
        iSize = _size.toInt();
        String str = data.substring(i+1,i+1+iSize);
        strcpy(buf,str.c_str());
      }
      return iSize;
    }else{
      while(mySerial.read() >= 0){}
    }
  }
  return 0;
}

void setup() {
  // put your setup code here, to run once:
  pinMode(led_pin,OUTPUT);
  
  Serial.begin(9600);; 
  mySerial.begin(9600);
  delay(1000);
  //多连接模式指令
  mySerial.println("AT+CIPMUX=1");
  delay(2000);
  //开启服务器模式： 8080为端口号
  mySerial.println("AT+CIPSERVER=1,8080");
  delay(3000);
  while(Serial.read() >= 0){}
  while(mySerial.read() >= 0){}
  Serial.println("Wificar init");
}

void loop() {
  // put your main code here, to run repeatedly:
  int leng = SerialData(ch);
  if(leng>0){
    if(strcmp(ch,"on")==0){
      digitalWrite(led_pin,HIGH);
      Serial.println("ON");
    }
    else if(strcmp(ch,"off") == 0){
      digitalWrite(led_pin,LOW);
      Serial.println("OFF");
    }
  }
}
