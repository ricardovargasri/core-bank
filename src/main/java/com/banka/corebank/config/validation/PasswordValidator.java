package com.banka.corebank.config.validation;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.stereotype.Component;

import com.banka.corebank.exception.InvalidPasswordException;

import java.util.ArrayList;
import java.util.List;

@Component
public class PasswordValidator {

    public void validate(String password, String firstName, String lastName, String email) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("La contraseña es obligatoria");
        }

        // Validación manual de nombres antes de Passay para mayor control
        String passLower = password.toLowerCase();
        if (passLower.contains(firstName.toLowerCase()) || passLower.contains(lastName.toLowerCase())) {
            throw new InvalidPasswordException("La contraseña no puede contener tu nombre o apellido por seguridad");
        }

        if (passLower.contains(email.toLowerCase().split("@")[0])) {
            throw new InvalidPasswordException("La contraseña no puede contener partes de tu correo electrónico");
        }

        List<Rule> rules = new ArrayList<>();

        // Reglas de validación
        rules.add(new LengthRule(12, 128)); // Longitud entre 12 y 128 caracteres
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1)); // Al menos 1 mayúscula
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1)); // Al menos 1 minúscula
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 1)); // Al menos 1 número
        rules.add(new CharacterRule(EnglishCharacterData.Special, 1)); // Al menos 1 carácter especial
        rules.add(new WhitespaceRule()); // No permite espacios en blanco

        // Reglas de secuencias prohibidas
        rules.add(new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false));
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false));

        // Validación
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(rules);
        RuleResult result = validator.validate(new PasswordData(password));

        if (!result.isValid()) {
            String errorMessage = validator.getMessages(result).stream()
                    .map(this::translateToSpanish)
                    .collect(java.util.stream.Collectors.joining(", "));

            throw new InvalidPasswordException(
                    "La contraseña no cumple con los requisitos de seguridad: " + errorMessage);
        }
    }

    private String translateToSpanish(String message) {
        return message
                .replace("Password must be 12 or more characters in length.", "debe tener al menos 12 caracteres")
                .replace("Password must contain 1 or more uppercase characters.", "debe incluir al menos 1 mayúscula")
                .replace("Password must contain 1 or more lowercase characters.", "debe incluir al menos 1 minúscula")
                .replace("Password must contain 1 or more digit characters.", "debe incluir al menos 1 número")
                .replace("Password must contain 1 or more special characters.",
                        "debe incluir al menos 1 carácter especial")
                .replace("cannot contain whitespace characters", "no puede contener espacios en blanco")
                .replace("contains the illegal sequence", "contiene la secuencia no permitida")
                .replace("qwerty", "qwerty (secuencia de teclado)")
                .replace("numerical", "numérica (secuencia)");
    }
}