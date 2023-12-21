# KScript
KScript is a simple scripting language written in java.

```c++
/*
Define script schema
*/
:schema 1

/*
Import system library
*/
this#def [system]

/*
REQUIRED main method
*/
main() {
  system#println('Hello world!')
  exit 1

  /*
  Exit with code 1
  */

  system#exit(1, 'My message on the exit message')
  /*
  Alternatively, exit with a custom message
  */
}
```

```c++
:schema 1

main() {
  sayHello('This is a multi-parameter ', 'message')
  printLine(false)
}

sayHello(message::) {
  echo [message]
}

printLine(variable) {
  echo I'll print literally this, without parsing "variable"
}
```
