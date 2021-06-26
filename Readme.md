Proyecto Hecho por: Jose Frncisco Palma Sanchez.

Este proyecto se trata de una API Rest elaborada en java, es para administrar la conexiones y las bases de datos usando HTTServer y SQLiIte.

Lamentablemente no pude comprobar el funcionamiento del proyecto por cuestiones de que habían varía fallas, la primer fallar que pude presenciar es que al momento de querer ensamblar la dependencia y empaquetar el proyecto con el comando (mvn assembly:assembly package) me daba error y no me dejaba hacer la acción, creo que el problema surge desde el dispositivo en el cual trabajo, no se si ubo un error en la instalación de los 2 software que utilizamos para trabajar o se daño algun archivo importante y tambien me surgio un error que el código no funcionaba correctamente por cuestión de que me pedia actualizar el JDK(Lo actualice pero daba cierto errores). por ese motivo no pude comprobar el funcionamiento del proyecto porque me surgieron varios problemas en la elaboración.

uso.

Se supone que para utilizarlo debemos poner este link

localhost:8000/api/v1/todos

que este caso me mandó lo siguiente:

{
"todos": [
{
"id": 1,
"title": "Hola Mundo!",
"completed": false
}
]
}

tanto en el postman como en el intellij

esta paste aparentemente si funciona correctamente.

<img src="/resources/c1.PNG"/>
<img src="/resources/c2.PNG"/>
