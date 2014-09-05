// Loaded by ScriptEngine if JavaScript

importClass(java.io.FileReader);

function load(file) {
	engine.eval(new FileReader(file));
}