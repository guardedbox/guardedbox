import i18next from 'i18next';
import translation_en from 'i18n/translations/en.json'

i18next.init({
    lng: 'en',
    resources: {
        en: { translation: translation_en }
    },
    interpolation: {
        escapeValue: false
    }
});
