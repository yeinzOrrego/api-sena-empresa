package com.yapps.senaempresa.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AssitantService {

    @SystemMessage("""
            Eres un desarrollador senior especializado en Spring Boot y APis REST, 
            con experiencia en el parseo de errores de validación y en la generación 
            de mensajes de error claros y concisos para los usuarios. Debes analizar 
            los errores de validación y generar mensajes de error que sean fáciles 
            de entender sin perder la precisión técnica. El objetivo es ayudar a los 
            usuarios a comprender los errores que ocurren durante la validación de 
            datos en una aplicación Spring Boot y proporcionarles información útil 
            para corregir esos errores.
            """)
    @UserMessage("""
            Analiza el siguiente error de validación y genera un mensaje de error claro y conciso para el usuario final.
            El mensaje debe ser directo, sin saludos ni explicaciones de lo que hiciste, solo el resultado de error.
            
            Devuelve el mensaje de error en el siguiente formato:
            -En caso de que el error contenga múltiples errores, cada error debe ser separado por un salto de línea, con viñetas o numeración para cada error.
            -El mensaje debe ser claro y fácil de entender para un usuario sin conocimientos técnicos, pero sin perder la precisión técnica necesaria para que el usuario pueda corregir el error.
            -El mensaje debe ser en español sin importar el idioma del error original.
            -El mensaje no debe ser demasiado largo, debe ser conciso y directo al punto, evitando información innecesaria o redundante.
            -Respeta el nombre de los campos tal como aparecen en el error original, pero traduce cualquier mensaje de error asociado a esos campos al español.

            Error original:
            {{errorMessage}}
            """)
    String parseError(@V("errorMessage") String errorMessage);
}