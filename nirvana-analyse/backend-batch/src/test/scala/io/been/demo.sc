println("hello, world")
val name = "been"
name

val numArray = new Array[Int](10)
val strString = new Array[String](10)
strString(0) = "String"
strString
val strArr = Array("first", "second")

import scala.collection.mutable.ArrayBuffer

val stringArrayVar = ArrayBuffer[String]()
stringArrayVar += " first"
stringArrayVar += "second"
stringArrayVar += ("hello", "world")
stringArrayVar ++= Array("been")
stringArrayVar ++ List("welcome")
stringArrayVar.trimEnd(3)
stringArrayVar
stringArrayVar.insert(0, "been")
stringArrayVar
for(i <- 0 until  stringArrayVar.length){
  println(stringArrayVar(i))
}

val arr = Array(1, 2 ,3 ,4 ,5, 6)
arr.sum
arr.sorted
arr.max
arr.min
arr.mkString(",")
arr.mkString("]")

case class Person(name: String, age: Int)
val p = new Person("been", 22)
p.name
p.age

arr.mkString("<", ",", ">")
val list = List(1, 2, 3, 4)
list(3)


list.isEmpty

list.reverse
val increase = (x: Int) => x + 1
println((increase(10)))

def increase2 (x: Int) = x + 1
println(increase2(4))
Array(1, 2, 3, 4, 5).map(increase).mkString(",")
Array(1, 2, 3, 4, 5) map (_ + 1) mkString(",")

def convert(f: Int => String ) = f (4)
convert((x :Int) => x +  " s")
def multiplyBy(factor: Double) = (x:Double) => factor  * x
val x= multiplyBy(10)
x(50)


