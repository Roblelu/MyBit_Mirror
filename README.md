# 🧠 MyBit Mirror — Teclado emocional inteligente

**MyBit Mirror** es un teclado experimental diseñado para interpretar el estado emocional del usuario en tiempo real a partir de sus mensajes escritos.  
Combina un backend en Python con Flask y un sistema de análisis semántico que responde con:

- 🎨 Un **color emocional** codificado en hexadecimal  
- 💬 Una **emoción detectada** basada en patrones psicológicos  
- 🪞 Una **reflexión interpretativa**, diseñada para funcionar como un espejo emocional

Este proyecto forma parte del sistema de interacción emocional del universo **YouYo**, enfocado en inteligencia artificial con sensibilidad narrativa y afectiva.

---

## 🚀 Objetivos

- Detectar emociones más allá de palabras clave.
- Responder con interpretaciones simbólicas y poéticas.
- Usarse como herramienta de autoexploración emocional o integración con interfaces sensibles (apps, bots, entornos VR, etc).

---

## 🔧 Tecnologías

- Python 3.13  
- Flask + Flask-CORS  
- Postman (pruebas de API)  
- JSON como canal de entrada/salida

---

## ▶️ Instalación rápida

```bash
git clone https://github.com/tu_usuario/mybit-mirror.git
cd mybit-mirror
pip install flask flask-cors
python app.py
```

---

## 🧪 Cómo usar

1. Ejecuta el servidor con `python app.py`
2. Abre Postman y realiza una petición `POST` a:

```
http://127.0.0.1:5000/analizar
```

Con el siguiente JSON:

```json
{
  "texto": "Me siento confundida pero quiero seguir."
}
```

3. Recibirás un JSON de respuesta con la emoción, el color y una reflexión interpretativa.

---

## 🖤 Nota

Este teclado **no busca diagnosticar** ni reemplazar procesos terapéuticos. Su propósito es ofrecer un reflejo simbólico y personalizado de las emociones transmitidas por texto.

---

Desarrollado con 💬 y psicología narrativa para el proyecto **YouYo**.
