# KScript
KScript is a simple scripting language written in java.

```
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
