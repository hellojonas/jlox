# jlox

An interpreter for the lox programming language built in java

## How to run

Add execute permission for the jlox script

```bash
chmod u+x ./jlox
```

To run the prompt just run the jlox script

```bash
./jlox
```

To execute a lox file pass the file as the argument to the jlox script

```bash
./jlox <source_file>
```

## Features

### variables

```js
var foo = "bar";
```

### Functions

```js
fun myFunc(param1, param2, ...) {
    // do something
}
```

### Branching

```js
var value = 10;
if (value > 10 and value < 20) {
    print "between 10 and 20";
} else {
    print "¯\_(")_/¯";
}
```

### Loops

```js
var i = 0;
while (i < 10) {
    print i;
    i = i + 1;
}

for (var i = 0; i < 10; i = i + 1) {
    print i;
}
```

### Closure

```js
fun makeCounter(s) {
    var i = s;

    fun counter() {
        i = i + 1;
        return i;
    }

    return counter;
}
```
