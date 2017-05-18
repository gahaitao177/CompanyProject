package com.caiyi.financial.nirvana.ccard.material.banks.minsheng;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
/***思路*/

/***主要是自己动态新建一个三个js文件 然后调用JS文件  获取 值 ***/

public class RunJS {
	 public static void main(String[] args){
		 RunJS a = new RunJS();
		 a.setkey("360730199001140312");
		 a.setexponent("010001");
		 a.setmodulus("008a4006035728fddbec2a1d99bc99ecf620331554d7291a953429731f5b2c396c88f1f8497fd916260cfc6ff4dd6a313c5deef8fecc9ab576815fb05f033597e434f57f9750e3f1b37f3dd78130491c959523882aff1d919f86715823b284a0db859672ebb9d50ac3b63236879ae012e7100aa574b41317abbd6826a2503402fd");
		 a.Run_JS();

		 a.Rsa_JS();
		 a.security_JS();
		 a.BigInt_JS();
		 a.base64encode_JS();


		 a.RunJsFun();
			
	 }
	 private String modulus;
	 private String exponent;
	 private String  key;
	 private String runJsName; // 运行的 jsname；
	 private String Filepath =   "/opt/export/data/";

	 public RunJS(){
		 buildpath();
	 }
	 public  void setmodulus(String modulus){
		 this.modulus = modulus;
	 }
     public  String getmodulus(){
		 return this.modulus;
	 }
     public  void setkey(String key){
		 this.key = key;
	 }
     public  String getkey(){
		 return this.key;
	 }
     
     public  void setexponent(String exponent){
		 this.exponent = exponent;
	 }
     public  String getexponent(){
		 return this.exponent;
	 }
	 
	 
	/***首先 运行JS 函数***/
	public  String RunJsFun(){
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		 String  result =null;
		try {
			
		
		     FileReader reader1 = new FileReader( Filepath + "BigIn.js");
		     FileReader reader2 = new FileReader( Filepath +  "RSA.js");
		     FileReader reader3 = new FileReader( Filepath  + "security.js");

			FileReader reader4 = new FileReader( Filepath  + "base64encode.js");
		     FileReader reader5= new FileReader( Filepath + runJsName);

			engine.eval(reader4);
		         engine.eval(reader1);
		         engine.eval(reader2);
		        engine.eval(reader3);


		       result = (String)engine.eval(reader5);
		  
		     System.out.println("result:" + result );
			reader1.close();
		       reader2.close();
		       reader3.close();
		       reader4.close();
			   reader5.close();
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		  /***删除文件****/
		deleteFile(runJsName);
		return result;
		
	}
	/****新建pat*/
	public void buildpath(){
		File file =new File(Filepath);

		if  (!file .exists()  && !file .isDirectory())
			{
			    System.out.println("//不存在");
			   boolean res=   file .mkdir();

				System.out.println("res" + res);
			} else
		    {
			    System.out.println("//目录存在");
			}



	}

	/***新建js文件**/
	public  Boolean buildJsFile(String fileName, String data ){
		Boolean res = true;
		try{



			fileName = Filepath + fileName;

		    
		      File file =new File(fileName);
		      if(fileName ==null || data == null){
		    	  return false;
		      }
			/***首先处理特殊*/
              if(fileName.contains(runJsName)){
            	  if(!file.exists()){

            		  file.createNewFile();
            	  }

				  FileWriter fileWritter = new FileWriter(file.getAbsoluteFile(),false);
 	             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				  bufferWritter.write(data);

 	             bufferWritter.close();
 	           //  System.out.println("Runjs 生成");
 	             return true;
		    	  
		      }
		      /**不存在的话 就写入***/
		      if(!file.exists()){
		       file.createNewFile();
		       FileWriter fileWritter = new FileWriter(file.getAbsoluteFile(),true);
	             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	             bufferWritter.write(data);
	             bufferWritter.close();
	            
		      }
		         
		     }catch(IOException e){
		      e.printStackTrace();
		      res = false;
		     }
		return res;
	}
	/**删除文件*/
	public void deleteFile(String fileName){
		fileName = Filepath + fileName;
		File f = new File( fileName);
		if(f.exists())
		 f.delete(); 
	}
/***下面开始新建验证需要的 四个JS文件*****/
	// 新建RSAjs文件
	public  void Rsa_JS(){
		String jsStr= 
		"RSAKeyPair;"+
		"function RSAKeyPair(encryptionExponent, decryptionExponent, modulus) {"+
		    "this.e = biFromHex(encryptionExponent);"+
		    "this.d = biFromHex(decryptionExponent);"+
		    "this.m = biFromHex(modulus);"+
		    "this.digitSize = 2 * biHighIndex(this.m) + 2;"+
		    "this.chunkSize = this.digitSize - 11;"+
		    "this.radix = 16;"+
		    "this.barrett = new BarrettMu(this.m)"+
		"}"+
		"function twoDigit(n) {"+
		    "return (n < 10 ? '0': '') + String(n)"+
		"}"+
		"function encryptedString(key, s) {"+
		    "if (key.chunkSize > key.digitSize - 11) {"+
		        "return 'Error'"+
		    "}"+
		   " var a = new Array();"+
		   "var sl = s.length;"+
		   " var i = 0;"+
		   " while (i < sl) {"+
		        "a[i] = s.charCodeAt(i);"+
		        "i++"+
		    "}"+
		   " var al = a.length;"+
		    "var result = '';"+
		   " var j, k, block;"+
		    "for (i = 0; i < al; i += key.chunkSize) {"+
		        "block = new BigInt();"+
		       " j = 0;"+
		       " var x;"+
		       " var msgLength = (i + key.chunkSize) > al ? al % key.chunkSize: key.chunkSize;"+
		        "var b = new Array();"+
		        "for (x = 0; x < msgLength; x++) {"+
		         "   b[x] = a[i + msgLength - 1 - x]"+
		        "}"+
		        "b[msgLength] = 0;"+
		        "var paddedSize = Math.max(8, key.digitSize - 3 - msgLength);"+
		        "for (x = 0; x < paddedSize; x++) {"+
		            "b[msgLength + 1 + x] = Math.floor(Math.random() * 254) + 1"+
		        "}"+
		        "b[key.digitSize - 2] = 2;"+
		        "b[key.digitSize - 1] = 0;"+
		        "for (k = 0; k < key.digitSize; ++j) {"+
		           " block.digits[j] = b[k++];"+
		           " block.digits[j] += b[k++] << 8"+
		        "}"+
		       " var crypt = key.barrett.powMod(block, key.e);"+
		       " var text = key.radix == 16 ? biToHex(crypt) : biToString(crypt, key.radix);"+
		        "result += text + ' '"+
		    "}"+
		    "return result.substring(0, result.length - 1)"+
		"}"+
		"function decryptedString(key, s) {"+
		   " var blocks = s.split(' ');"+
		   " var result = '';"+
		   " var i, j, block;"+
		   " for (i = 0; i < blocks.length; ++i) {"+
		    "    var bi;"+
		    "    if (key.radix == 16) {"+
		     "       bi = biFromHex(blocks[i])"+
		     "   } else {"+
		    "       bi = biFromString(blocks[i], key.radix)"+
		    "    }"+
		     "   block = key.barrett.powMod(bi, key.d);"+
		    "    for (j = 0; j <= biHighIndex(block); ++j) {"+
		    "        result += String.fromCharCode(block.digits[j] & 255, block.digits[j] >> 8)"+
		    "    }"+
		   " }"+
		    "if (result.charCodeAt(result.length - 1) == 0) {"+
		  "      result = result.substring(0, result.length - 1)"+
		  "  }"+
		   " return result"+
		"};";
		buildJsFile("RSA.js",jsStr );
		
	}
	// 新建security.js
	public void security_JS(){
		String security= "var RSAUtils={};var biRadixBase=2;var biRadixBits=16;var bitsPerDigit=biRadixBits;var biRadix=1<<16;var biHalfRadix=biRadix>>>1;var biRadixSquared=biRadix*biRadix;var maxDigitVal=biRadix-1;var maxInteger=9999999999999998;var maxDigits;var ZERO_ARRAY;var bigZero,bigOne;var BigInt=function(flag){if(typeof flag=='boolean'&&flag==true){this.digits=null}else{this.digits=ZERO_ARRAY.slice(0)}this.isNeg=false};RSAUtils.setMaxDigits=function(value){maxDigits=value;ZERO_ARRAY=new Array(maxDigits);for(var iza=0;iza<ZERO_ARRAY.length;iza++){ZERO_ARRAY[iza]=0}bigZero=new BigInt();bigOne=new BigInt();bigOne.digits[0]=1};RSAUtils.setMaxDigits(20);var dpl10=15;RSAUtils.biFromNumber=function(i){var result=new BigInt();result.isNeg=i<0;i=Math.abs(i);var j=0;while(i>0){result.digits[j++]=i&maxDigitVal;i=Math.floor(i/biRadix)}return result};var lr10=RSAUtils.biFromNumber(1000000000000000);RSAUtils.biFromDecimal=function(s){var isNeg=s.charAt(0)=='-';var i=isNeg?1:0;var result;while(i<s.length&&s.charAt(i)=='0'){++i}if(i==s.length){result=new BigInt()}else{var digitCount=s.length-i;var fgl=digitCount%dpl10;if(fgl==0){fgl=dpl10}result=RSAUtils.biFromNumber(Number(s.substr(i,fgl)));i+=fgl;while(i<s.length){result=RSAUtils.biAdd(RSAUtils.biMultiply(result,lr10),RSAUtils.biFromNumber(Number(s.substr(i,dpl10))));i+=dpl10}result.isNeg=isNeg}return result};RSAUtils.biCopy=function(bi){var result=new BigInt(true);result.digits=bi.digits.slice(0);result.isNeg=bi.isNeg;return result};RSAUtils.reverseStr=function(s){var result='';for(var i=s.length-1;i>-1;--i){result+=s.charAt(i)}return result};var hexatrigesimalToChar=['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];RSAUtils.biToString=function(x,radix){var b=new BigInt();b.digits[0]=radix;var qr=RSAUtils.biDivideModulo(x,b);var result=hexatrigesimalToChar[qr[1].digits[0]];while(RSAUtils.biCompare(qr[0],bigZero)==1){qr=RSAUtils.biDivideModulo(qr[0],b);digit=qr[1].digits[0];result+=hexatrigesimalToChar[qr[1].digits[0]]}return(x.isNeg?'-':'')+RSAUtils.reverseStr(result)};RSAUtils.biToDecimal=function(x){var b=new BigInt();b.digits[0]=10;var qr=RSAUtils.biDivideModulo(x,b);var result=String(qr[1].digits[0]);while(RSAUtils.biCompare(qr[0],bigZero)==1){qr=RSAUtils.biDivideModulo(qr[0],b);result+=String(qr[1].digits[0])}return(x.isNeg?'-':'')+RSAUtils.reverseStr(result)};var hexToChar=['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'];RSAUtils.digitToHex=function(n)"+
				"{var mask=15;var result='';for(i=0;i<4;++i){result+=hexToChar[n&mask];n>>>=4}return RSAUtils.reverseStr(result)};RSAUtils.biToHex=function(x){var result='';var n=RSAUtils.biHighIndex(x);for(var i=RSAUtils.biHighIndex(x);i>-1;--i){result+=RSAUtils.digitToHex(x.digits[i])}return result};RSAUtils.charToHex=function(c){var ZERO=48;var NINE=ZERO+9;var littleA=97;var littleZ=littleA+25;var bigA=65;var bigZ=65+25;var result;if(c>=ZERO&&c<=NINE){result=c-ZERO}else{if(c>=bigA&&c<=bigZ){result=10+c-bigA}else{if(c>=littleA&&c<=littleZ){result=10+c-littleA}else{result=0}}}return result};RSAUtils.hexToDigit=function(s){var result=0;var sl=Math.min(s.length,4);for(var i=0;i<sl;++i){result<<=4;result|=RSAUtils.charToHex(s.charCodeAt(i))}return result};RSAUtils.biFromHex=function(s){var result=new BigInt();var sl=s.length;for(var i=sl,j=0;i>0;i-=4,++j){result.digits[j]=RSAUtils.hexToDigit(s.substr(Math.max(i-4,0),Math.min(i,4)))}return result};RSAUtils.biFromString=function(s,radix){var isNeg=s.charAt(0)=='-';var istop=isNeg?1:0;var result=new BigInt();var place=new BigInt();place.digits[0]=1;for(var i=s.length-1;i>=istop;i--){var c=s.charCodeAt(i);var digit=RSAUtils.charToHex(c);var biDigit=RSAUtils.biMultiplyDigit(place,digit);result=RSAUtils.biAdd(result,biDigit);place=RSAUtils.biMultiplyDigit(place,radix)}result.isNeg=isNeg;return result};RSAUtils.biDump=function(b){return(b.isNeg?'-':'')+b.digits.join(' ')};RSAUtils.biAdd=function(x,y){var result;if(x.isNeg!=y.isNeg){y.isNeg=!y.isNeg;result=RSAUtils.biSubtract(x,y);y.isNeg=!y.isNeg}else{result=new BigInt();var c=0;var n;for(var i=0;i<x.digits.length;++i){n=x.digits[i]+y.digits[i]+c;result.digits[i]=n%biRadix;c=Number(n>=biRadix)}result.isNeg=x.isNeg}return result};RSAUtils.biSubtract=function(x,y){var result;if(x.isNeg!=y.isNeg){y.isNeg=!y.isNeg;result=RSAUtils.biAdd(x,y);y.isNeg=!y.isNeg}else{result=new BigInt();var n,c;c=0;for(var i=0;i<x.digits.length;++i){n=x.digits[i]-y.digits[i]+c;result.digits[i]=n%biRadix;if(result.digits[i]<0){result.digits[i]+=biRadix}c=0-Number(n<0)}if(c==-1){c=0;for(var i=0;i<x.digits.length;++i){n=0-result.digits[i]+c;result.digits[i]=n%biRadix;if(result.digits[i]<0){result.digits[i]+=biRadix}c=0-Number(n<0)}result.isNeg=!x.isNeg}else{result.isNeg=x.isNeg}}return result};RSAUtils.biHighIndex=function(x){var result=x.digits.length-1;while(result>0&&x.digits[result]==0){--result}return result};RSAUtils.biNumBits=function(x){var n=RSAUtils.biHighIndex(x);var d=x.digits[n];"+
				"var m=(n+1)*bitsPerDigit;var result;for(result=m;result>m-bitsPerDigit;--result){if((d&32768)!=0){break}d<<=1}return result};RSAUtils.biMultiply=function(x,y){var result=new BigInt();var c;var n=RSAUtils.biHighIndex(x);var t=RSAUtils.biHighIndex(y);var u,uv,k;for(var i=0;i<=t;++i){c=0;k=i;for(j=0;j<=n;++j,++k){uv=result.digits[k]+x.digits[j]*y.digits[i]+c;result.digits[k]=uv&maxDigitVal;c=uv>>>biRadixBits}result.digits[i+n+1]=c}result.isNeg=x.isNeg!=y.isNeg;return result};RSAUtils.biMultiplyDigit=function(x,y){var n,c,uv;result=new BigInt();n=RSAUtils.biHighIndex(x);c=0;for(var j=0;j<=n;++j){uv=result.digits[j]+x.digits[j]*y+c;result.digits[j]=uv&maxDigitVal;c=uv>>>biRadixBits}result.digits[1+n]=c;return result};RSAUtils.arrayCopy=function(src,srcStart,dest,destStart,n){var m=Math.min(srcStart+n,src.length);for(var i=srcStart,j=destStart;i<m;++i,++j){dest[j]=src[i]}};var highBitMasks=[0,32768,49152,57344,61440,63488,64512,65024,65280,65408,65472,65504,65520,65528,65532,65534,65535];RSAUtils.biShiftLeft=function(x,n){var digitCount=Math.floor(n/bitsPerDigit);var result=new BigInt();RSAUtils.arrayCopy(x.digits,0,result.digits,digitCount,result.digits.length-digitCount);var bits=n%bitsPerDigit;var rightBits=bitsPerDigit-bits;for(var i=result.digits.length-1,i1=i-1;i>0;--i,--i1){result.digits[i]=((result.digits[i]<<bits)&maxDigitVal)|((result.digits[i1]&highBitMasks[bits])>>>(rightBits))}result.digits[0]=((result.digits[i]<<bits)&maxDigitVal);result.isNeg=x.isNeg;return result};var lowBitMasks=[0,1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535];RSAUtils.biShiftRight=function(x,n){var digitCount=Math.floor(n/bitsPerDigit);var result=new BigInt();RSAUtils.arrayCopy(x.digits,digitCount,result.digits,0,x.digits.length-digitCount);var bits=n%bitsPerDigit;var leftBits=bitsPerDigit-bits;for(var i=0,i1=i+1;i<result.digits.length-1;++i,++i1){result.digits[i]=(result.digits[i]>>>bits)|((result.digits[i1]&lowBitMasks[bits])<<leftBits)}result.digits[result.digits.length-1]>>>=bits;result.isNeg=x.isNeg;return result};RSAUtils.biMultiplyByRadixPower=function(x,n){var result=new BigInt();RSAUtils.arrayCopy(x.digits,0,result.digits,n,result.digits.length-n);return result};RSAUtils.biDivideByRadixPower=function(x,n){var result=new BigInt();RSAUtils.arrayCopy(x.digits,n,result.digits,0,result.digits.length-n);return result};RSAUtils.biModuloByRadixPower=function(x,n){var result=new BigInt();RSAUtils.arrayCopy(x.digits,0,result.digits,0,n);"+
				"return result};RSAUtils.biCompare=function(x,y){if(x.isNeg!=y.isNeg){return 1-2*Number(x.isNeg)}for(var i=x.digits.length-1;i>=0;--i){if(x.digits[i]!=y.digits[i]){if(x.isNeg){return 1-2*Number(x.digits[i]>y.digits[i])}else{return 1-2*Number(x.digits[i]<y.digits[i])}}}return 0};RSAUtils.biDivideModulo=function(x,y){var nb=RSAUtils.biNumBits(x);var tb=RSAUtils.biNumBits(y);var origYIsNeg=y.isNeg;var q,r;if(nb<tb){if(x.isNeg){q=RSAUtils.biCopy(bigOne);q.isNeg=!y.isNeg;x.isNeg=false;y.isNeg=false;r=biSubtract(y,x);x.isNeg=true;y.isNeg=origYIsNeg}else{q=new BigInt();r=RSAUtils.biCopy(x)}return[q,r]}q=new BigInt();r=x;var t=Math.ceil(tb/bitsPerDigit)-1;var lambda=0;while(y.digits[t]<biHalfRadix){y=RSAUtils.biShiftLeft(y,1);++lambda;++tb;t=Math.ceil(tb/bitsPerDigit)-1}r=RSAUtils.biShiftLeft(r,lambda);nb+=lambda;var n=Math.ceil(nb/bitsPerDigit)-1;var b=RSAUtils.biMultiplyByRadixPower(y,n-t);while(RSAUtils.biCompare(r,b)!=-1){++q.digits[n-t];r=RSAUtils.biSubtract(r,b)}for(var i=n;i>t;--i){var ri=(i>=r.digits.length)?0:r.digits[i];var ri1=(i-1>=r.digits.length)?0:r.digits[i-1];var ri2=(i-2>=r.digits.length)?0:r.digits[i-2];var yt=(t>=y.digits.length)?0:y.digits[t];var yt1=(t-1>=y.digits.length)?0:y.digits[t-1];if(ri==yt){q.digits[i-t-1]=maxDigitVal}else{q.digits[i-t-1]=Math.floor((ri*biRadix+ri1)/yt)}var c1=q.digits[i-t-1]*((yt*biRadix)+yt1);var c2=(ri*biRadixSquared)+((ri1*biRadix)+ri2);while(c1>c2){--q.digits[i-t-1];c1=q.digits[i-t-1]*((yt*biRadix)|yt1);c2=(ri*biRadix*biRadix)+((ri1*biRadix)+ri2)}b=RSAUtils.biMultiplyByRadixPower(y,i-t-1);r=RSAUtils.biSubtract(r,RSAUtils.biMultiplyDigit(b,q.digits[i-t-1]));if(r.isNeg){r=RSAUtils.biAdd(r,b);--q.digits[i-t-1]}}r=RSAUtils.biShiftRight(r,lambda);q.isNeg=x.isNeg!=origYIsNeg;if(x.isNeg){if(origYIsNeg){q=RSAUtils.biAdd(q,bigOne)}else{q=RSAUtils.biSubtract(q,bigOne)}y=RSAUtils.biShiftRight(y,lambda);r=RSAUtils.biSubtract(y,r)}if(r.digits[0]==0&&RSAUtils.biHighIndex(r)==0){r.isNeg=false}return[q,r]};RSAUtils.biDivide=function(x,y){return RSAUtils.biDivideModulo(x,y)[0]};RSAUtils.biModulo=function(x,y){return RSAUtils.biDivideModulo(x,y)[1]};RSAUtils.biMultiplyMod=function(x,y,m){return RSAUtils.biModulo(RSAUtils.biMultiply(x,y),m)};RSAUtils.biPow=function(x,y){var result=bigOne;var a=x;while(true){if((y&1)!=0){result=RSAUtils.biMultiply(result,a)}y>>=1;if(y==0){break}a=RSAUtils.biMultiply(a,a)}return result};RSAUtils.biPowMod=function(x,y,m){var result=bigOne;var a=x;var k=y;while(true){if((k.digits[0]&1)!=0){result=RSAUtils.biMultiplyMod(result,a,m)"+
				"}k=RSAUtils.biShiftRight(k,1);if(k.digits[0]==0&&RSAUtils.biHighIndex(k)==0){break}a=RSAUtils.biMultiplyMod(a,a,m)}return result};BarrettMu=function(m){this.modulus=RSAUtils.biCopy(m);this.k=RSAUtils.biHighIndex(this.modulus)+1;var b2k=new BigInt();b2k.digits[2*this.k]=1;this.mu=RSAUtils.biDivide(b2k,this.modulus);this.bkplus1=new BigInt();this.bkplus1.digits[this.k+1]=1;this.modulo=BarrettMu_modulo;this.multiplyMod=BarrettMu_multiplyMod;this.powMod=BarrettMu_powMod};function BarrettMu_modulo(x){var $dmath=RSAUtils;var q1=$dmath.biDivideByRadixPower(x,this.k-1);var q2=$dmath.biMultiply(q1,this.mu);var q3=$dmath.biDivideByRadixPower(q2,this.k+1);var r1=$dmath.biModuloByRadixPower(x,this.k+1);var r2term=$dmath.biMultiply(q3,this.modulus);var r2=$dmath.biModuloByRadixPower(r2term,this.k+1);var r=$dmath.biSubtract(r1,r2);if(r.isNeg){r=$dmath.biAdd(r,this.bkplus1)}var rgtem=$dmath.biCompare(r,this.modulus)>=0;while(rgtem){r=$dmath.biSubtract(r,this.modulus);rgtem=$dmath.biCompare(r,this.modulus)>=0}return r}function BarrettMu_multiplyMod(x,y){var xy=RSAUtils.biMultiply(x,y);return this.modulo(xy)}function BarrettMu_powMod(x,y){var result=new BigInt();result.digits[0]=1;var a=x;var k=y;while(true){if((k.digits[0]&1)!=0){result=this.multiplyMod(result,a)}k=RSAUtils.biShiftRight(k,1);if(k.digits[0]==0&&RSAUtils.biHighIndex(k)==0){break}a=this.multiplyMod(a,a)}return result}var RSAKeyPair=function(encryptionExponent,decryptionExponent,modulus){var $dmath=RSAUtils;this.e=$dmath.biFromHex(encryptionExponent);this.d=$dmath.biFromHex(decryptionExponent);this.m=$dmath.biFromHex(modulus);this.chunkSize=2*$dmath.biHighIndex(this.m);this.radix=16;this.barrett=new BarrettMu(this.m)};RSAUtils.getKeyPair=function(encryptionExponent,decryptionExponent,modulus){return new RSAKeyPair(encryptionExponent,decryptionExponent,modulus)};if(typeof twoDigit==='undefined'){twoDigit=function(n){return(n<10?'0':'')+String(n)}}RSAUtils.encryptedString=function(key,s){var a=[];var sl=s.length;var i=0;while(i<sl){a[i]=s.charCodeAt(i);i++}while(a.length%key.chunkSize!=0){a[i++]=0}var al=a.length;var result='';var j,k,block;for(i=0;i<al;i+=key.chunkSize){block=new BigInt();j=0;for(k=i;k<i+key.chunkSize;++j){block.digits[j]=a[k++];block.digits[j]+=a[k++]<<8}var crypt=key.barrett.powMod(block,key.e);var text=key.radix==16?RSAUtils.biToHex(crypt):RSAUtils.biToString(crypt,key.radix);result+=text+' '}return result.substring(0,result.length-1)};RSAUtils.decryptedString=function(key,s)"+
				"{var blocks=s.split(' ');var result='';var i,j,block;for(i=0;i<blocks.length;++i){var bi;if(key.radix==16){bi=RSAUtils.biFromHex(blocks[i])}else{bi=RSAUtils.biFromString(blocks[i],key.radix)}block=key.barrett.powMod(bi,key.d);for(j=0;j<=RSAUtils.biHighIndex(block);++j){result+=String.fromCharCode(block.digits[j]&255,block.digits[j]>>8)}}if(result.charCodeAt(result.length-1)==0){result=result.substring(0,result.length-1)}return result};RSAUtils.setMaxDigits(130);";
		
		buildJsFile("security.js",security);
	}
	// 新建BigInt.js
	public  void BigInt_JS(){
		String bigInt ="var biRadixBase=2;var biRadixBits=16;var bitsPerDigit=biRadixBits;var biRadix=1<<16;var biHalfRadix=biRadix>>>1;var biRadixSquared=biRadix*biRadix;var maxDigitVal=biRadix-1;var maxInteger=9999999999999998;var maxDigits;var ZERO_ARRAY;var bigZero,bigOne;function setMaxDigits(value){maxDigits=value;ZERO_ARRAY=new Array(maxDigits);for(var iza=0;iza<ZERO_ARRAY.length;iza++){ZERO_ARRAY[iza]=0}bigZero=new BigInt();bigOne=new BigInt();bigOne.digits[0]=1}setMaxDigits(20);var dpl10=15;var lr10=biFromNumber(1000000000000000);function BigInt(flag){if(typeof flag=='boolean'&&flag==true){this.digits=null}else{this.digits=ZERO_ARRAY.slice(0)}this.isNeg=false}function biFromDecimal(s){var isNeg=s.charAt(0)=='-';var i=isNeg?1:0;var result;while(i<s.length&&s.charAt(i)=='0'){++i}if(i==s.length){result=new BigInt()}else{var digitCount=s.length-i;var fgl=digitCount%dpl10;if(fgl==0){fgl=dpl10}result=biFromNumber(Number(s.substr(i,fgl)));i+=fgl;while(i<s.length){result=biAdd(biMultiply(result,lr10),biFromNumber(Number(s.substr(i,dpl10))));i+=dpl10}result.isNeg=isNeg}return result}function biCopy(bi){var result=new BigInt(true);result.digits=bi.digits.slice(0);result.isNeg=bi.isNeg;return result}function biFromNumber(i){var result=new BigInt();result.isNeg=i<0;i=Math.abs(i);var j=0;while(i>0){result.digits[j++]=i&maxDigitVal;i=Math.floor(i/biRadix)}return result}function reverseStr(s){var result='';for(var i=s.length-1;i>-1;--i){result+=s.charAt(i)}return result}var hexatrigesimalToChar=new Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');function biToString(x,radix){var b=new BigInt();b.digits[0]=radix;var qr=biDivideModulo(x,b);var result=hexatrigesimalToChar[qr[1].digits[0]];while(biCompare(qr[0],bigZero)==1){qr=biDivideModulo(qr[0],b);digit=qr[1].digits[0];result+=hexatrigesimalToChar[qr[1].digits[0]]}return(x.isNeg?'-':'')+reverseStr(result)}function biToDecimal(x){var b=new BigInt();b.digits[0]=10;var qr=biDivideModulo(x,b);var result=String(qr[1].digits[0]);while(biCompare(qr[0],bigZero)==1){qr=biDivideModulo(qr[0],b);result+=String(qr[1].digits[0])}return(x.isNeg?'-':'')+reverseStr(result)}var hexToChar=new Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f');function digitToHex(n){var mask=15;var result='';for(i=0;i<4;++i){result+=hexToChar[n&mask];n>>>=4}return reverseStr(result)}function biToHex(x){var result='';var n=biHighIndex(x);"+
				"for(var i=biHighIndex(x);i>-1;--i){result+=digitToHex(x.digits[i])}return result}function charToHex(c){var ZERO=48;var NINE=ZERO+9;var littleA=97;var littleZ=littleA+25;var bigA=65;var bigZ=65+25;var result;if(c>=ZERO&&c<=NINE){result=c-ZERO}else{if(c>=bigA&&c<=bigZ){result=10+c-bigA}else{if(c>=littleA&&c<=littleZ){result=10+c-littleA}else{result=0}}}return result}function hexToDigit(s){var result=0;var sl=Math.min(s.length,4);for(var i=0;i<sl;++i){result<<=4;result|=charToHex(s.charCodeAt(i))}return result}function biFromHex(s){var result=new BigInt();var sl=s.length;for(var i=sl,j=0;i>0;i-=4,++j){result.digits[j]=hexToDigit(s.substr(Math.max(i-4,0),Math.min(i,4)))}return result}function biFromString(s,radix){var isNeg=s.charAt(0)=='-';var istop=isNeg?1:0;var result=new BigInt();var place=new BigInt();place.digits[0]=1;for(var i=s.length-1;i>=istop;i--){var c=s.charCodeAt(i);var digit=charToHex(c);var biDigit=biMultiplyDigit(place,digit);result=biAdd(result,biDigit);place=biMultiplyDigit(place,radix)}result.isNeg=isNeg;return result}function biDump(b){return(b.isNeg?'-':'')+b.digits.join(' ')}function biAdd(x,y){var result;if(x.isNeg!=y.isNeg){y.isNeg=!y.isNeg;result=biSubtract(x,y);y.isNeg=!y.isNeg}else{result=new BigInt();var c=0;var n;for(var i=0;i<x.digits.length;++i){n=x.digits[i]+y.digits[i]+c;result.digits[i]=n%biRadix;c=Number(n>=biRadix)}result.isNeg=x.isNeg}return result}function biSubtract(x,y){var result;if(x.isNeg!=y.isNeg){y.isNeg=!y.isNeg;result=biAdd(x,y);y.isNeg=!y.isNeg}else{result=new BigInt();var n,c;c=0;for(var i=0;i<x.digits.length;++i){n=x.digits[i]-y.digits[i]+c;result.digits[i]=n%biRadix;if(result.digits[i]<0){result.digits[i]+=biRadix}c=0-Number(n<0)}if(c==-1){c=0;for(var i=0;i<x.digits.length;++i){n=0-result.digits[i]+c;result.digits[i]=n%biRadix;if(result.digits[i]<0){result.digits[i]+=biRadix}c=0-Number(n<0)}result.isNeg=!x.isNeg}else{result.isNeg=x.isNeg}}return result}function biHighIndex(x){var result=x.digits.length-1;while(result>0&&x.digits[result]==0){--result}return result}function biNumBits(x){var n=biHighIndex(x);var d=x.digits[n];var m=(n+1)*bitsPerDigit;var result;for(result=m;result>m-bitsPerDigit;--result){if((d&32768)!=0){break}d<<=1}return result}function biMultiply(x,y){var result=new BigInt();var c;var n=biHighIndex(x);var t=biHighIndex(y);var u,uv,k;for(var i=0;i<=t;++i){c=0;k=i;for(j=0;j<=n;++j,++k){uv=result.digits[k]+x.digits[j]*y.digits[i]+c;result.digits[k]=uv&maxDigitVal;c=uv>>>biRadixBits}result.digits[i+n+1]=c}result.isNeg=x.isNeg!=y.isNeg;"+
				"return result}function biMultiplyDigit(x,y){var n,c,uv;result=new BigInt();n=biHighIndex(x);c=0;for(var j=0;j<=n;++j){uv=result.digits[j]+x.digits[j]*y+c;result.digits[j]=uv&maxDigitVal;c=uv>>>biRadixBits}result.digits[1+n]=c;return result}function arrayCopy(src,srcStart,dest,destStart,n){var m=Math.min(srcStart+n,src.length);for(var i=srcStart,j=destStart;i<m;++i,++j){dest[j]=src[i]}}var highBitMasks=new Array(0,32768,49152,57344,61440,63488,64512,65024,65280,65408,65472,65504,65520,65528,65532,65534,65535);function biShiftLeft(x,n){var digitCount=Math.floor(n/bitsPerDigit);var result=new BigInt();arrayCopy(x.digits,0,result.digits,digitCount,result.digits.length-digitCount);var bits=n%bitsPerDigit;var rightBits=bitsPerDigit-bits;for(var i=result.digits.length-1,i1=i-1;i>0;--i,--i1){result.digits[i]=((result.digits[i]<<bits)&maxDigitVal)|((result.digits[i1]&highBitMasks[bits])>>>(rightBits))}result.digits[0]=((result.digits[i]<<bits)&maxDigitVal);result.isNeg=x.isNeg;return result}var lowBitMasks=new Array(0,1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535);function biShiftRight(x,n){var digitCount=Math.floor(n/bitsPerDigit);var result=new BigInt();arrayCopy(x.digits,digitCount,result.digits,0,x.digits.length-digitCount);var bits=n%bitsPerDigit;var leftBits=bitsPerDigit-bits;for(var i=0,i1=i+1;i<result.digits.length-1;++i,++i1){result.digits[i]=(result.digits[i]>>>bits)|((result.digits[i1]&lowBitMasks[bits])<<leftBits)}result.digits[result.digits.length-1]>>>=bits;result.isNeg=x.isNeg;return result}function biMultiplyByRadixPower(x,n){var result=new BigInt();arrayCopy(x.digits,0,result.digits,n,result.digits.length-n);return result}function biDivideByRadixPower(x,n){var result=new BigInt();arrayCopy(x.digits,n,result.digits,0,result.digits.length-n);return result}function biModuloByRadixPower(x,n){var result=new BigInt();arrayCopy(x.digits,0,result.digits,0,n);return result}function biCompare(x,y){if(x.isNeg!=y.isNeg){return 1-2*Number(x.isNeg)}for(var i=x.digits.length-1;i>=0;--i){if(x.digits[i]!=y.digits[i]){if(x.isNeg){return 1-2*Number(x.digits[i]>y.digits[i])}else{return 1-2*Number(x.digits[i]<y.digits[i])}}}return 0}function biDivideModulo(x,y){var nb=biNumBits(x);var tb=biNumBits(y);var origYIsNeg=y.isNeg;var q,r;if(nb<tb){if(x.isNeg){q=biCopy(bigOne);q.isNeg=!y.isNeg;x.isNeg=false;y.isNeg=false;r=biSubtract(y,x);x.isNeg=true;y.isNeg=origYIsNeg}else{q=new BigInt();r=biCopy(x)}return new Array(q,r)}q=new BigInt();r=x;var t=Math.ceil(tb/bitsPerDigit)-1;"+
				"var lambda=0;while(y.digits[t]<biHalfRadix){y=biShiftLeft(y,1);++lambda;++tb;t=Math.ceil(tb/bitsPerDigit)-1}r=biShiftLeft(r,lambda);nb+=lambda;var n=Math.ceil(nb/bitsPerDigit)-1;var b=biMultiplyByRadixPower(y,n-t);while(biCompare(r,b)!=-1){++q.digits[n-t];r=biSubtract(r,b)}for(var i=n;i>t;--i){var ri=(i>=r.digits.length)?0:r.digits[i];var ri1=(i-1>=r.digits.length)?0:r.digits[i-1];var ri2=(i-2>=r.digits.length)?0:r.digits[i-2];var yt=(t>=y.digits.length)?0:y.digits[t];var yt1=(t-1>=y.digits.length)?0:y.digits[t-1];if(ri==yt){q.digits[i-t-1]=maxDigitVal}else{q.digits[i-t-1]=Math.floor((ri*biRadix+ri1)/yt)}var c1=q.digits[i-t-1]*((yt*biRadix)+yt1);var c2=(ri*biRadixSquared)+((ri1*biRadix)+ri2);while(c1>c2){--q.digits[i-t-1];c1=q.digits[i-t-1]*((yt*biRadix)|yt1);c2=(ri*biRadix*biRadix)+((ri1*biRadix)+ri2)}b=biMultiplyByRadixPower(y,i-t-1);r=biSubtract(r,biMultiplyDigit(b,q.digits[i-t-1]));if(r.isNeg){r=biAdd(r,b);--q.digits[i-t-1]}}r=biShiftRight(r,lambda);q.isNeg=x.isNeg!=origYIsNeg;if(x.isNeg){if(origYIsNeg){q=biAdd(q,bigOne)}else{q=biSubtract(q,bigOne)}y=biShiftRight(y,lambda);r=biSubtract(y,r)}if(r.digits[0]==0&&biHighIndex(r)==0){r.isNeg=false}return new Array(q,r)}function biDivide(x,y){return biDivideModulo(x,y)[0]}function biModulo(x,y){return biDivideModulo(x,y)[1]}function biMultiplyMod(x,y,m){return biModulo(biMultiply(x,y),m)}function biPow(x,y){var result=bigOne;var a=x;while(true){if((y&1)!=0){result=biMultiply(result,a)}y>>=1;if(y==0){break}a=biMultiply(a,a)}return result}function biPowMod(x,y,m){var result=bigOne;var a=x;var k=y;while(true){if((k.digits[0]&1)!=0){result=biMultiplyMod(result,a,m)}k=biShiftRight(k,1);if(k.digits[0]==0&&biHighIndex(k)==0){break}a=biMultiplyMod(a,a,m)}return result};";
		buildJsFile("BigIn.js",bigInt );
	}
	//  新建一个base64encode
	public  void base64encode_JS(){
		String base64encode =
				"function base64encode(str){var base64EncodeChars='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';"+
						"var base64DecodeChars=new Array(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,"+
						"-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,-1,0,1,2,"+
						"3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,-1,26,27,28,29,30,31,32,33,34,35,36,37,"+
						"38,39,40,41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1);var out,i,len;var c1,c2,c3;len=str.length;i=0;out='';"+
						"while(i<len){c1=str.charCodeAt(i++)&255;if(i==len){out+=base64EncodeChars.charAt(c1>>2);"+
						"out+=base64EncodeChars.charAt((c1&3)<<4);out+='==';break}c2=str.charCodeAt(i++);if(i==len){out+=base64EncodeChars.charAt(c1>>2);"+
						"out+=base64EncodeChars.charAt(((c1&3)<<4)|((c2&240)>>4));out+=base64EncodeChars.charAt((c2&15)<<2);out+='=';break}"+
						"c3=str.charCodeAt(i++);out+=base64EncodeChars.charAt(c1>>2);out+=base64EncodeChars.charAt(((c1&3)<<4)|((c2&240)>>4));"+
						"out+=base64EncodeChars.charAt(((c2&15)<<2)|((c3&192)>>6));out+=base64EncodeChars.charAt(c3&63)}return out};";

		buildJsFile("base64encode.js",base64encode );
	}



    //新建一个run.js 
	public void Run_JS(){
		String runjs =
	
		"var  exponent= "+"'" +exponent+"'" + ";"+
	    "var modulus = " +"'"+ modulus+"'"+ ";"+
	    "var key = RSAUtils.getKeyPair(exponent, '', modulus);"+
	    "var numb = " +"'"+  key +"'"+ ";" +
	     "var dnum = RSAUtils.encryptedString(key, base64encode(numb));" +
	    "dnum";


		// System.out.println(runjs);
		 long t= System.currentTimeMillis();
		 runJsName = t + ".js";
		 buildJsFile(runJsName,runjs);
	}
	
	/**新建完了 所有的js 就准备运行****/
	public String encryptedString(String data,String exponent, String modulus ){

		 /**设置值**/
		 setkey(data);
		 setexponent(exponent);
		 setmodulus(modulus);
		 /**建立要运行的JS**/
		 Run_JS();

		/***建立js文件*/
		Rsa_JS();
		security_JS();
		BigInt_JS();
		base64encode_JS();
		 /**运行JS**/
		return  RunJsFun();
	}
	
	
	
}
