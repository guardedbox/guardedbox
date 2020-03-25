import i18next from 'i18next';
import translation_en from 'i18n/translations/en.json';
import translation_es from 'i18n/translations/es.json';
import { currentLocationParams } from 'services/location.jsx';

i18next.init({
    lng: selectLanguage(),
    fallbackLng: 'en',
    load: 'languageOnly',
    resources: {
        en: { translation: translation_en },
        es: { translation: translation_es }
    },
    interpolation: {
        escapeValue: false
    }
});

function selectLanguage() {

    var urlParams = currentLocationParams();
    var langUrlParam = urlParams.lng || urlParams.lang || urlParams.language;

    return langUrlParam || navigator.language || navigator.userLanguage;

}
