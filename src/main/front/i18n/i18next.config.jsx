import i18next from 'i18next';
import translation_en from 'i18n/translations/en.json'
import translation_es from 'i18n/translations/es.json'

i18next.init({
    lng: 'es',
    resources: {
        en: { translation: translation_en },
        es: { translation: translation_es }
    },
    interpolation: {
        escapeValue: false
    }
});
