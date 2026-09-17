# 📖 Bíblia Todas Versões

Aplicativo Android moderno para leitura da Bíblia, criado por **Thalyssonjsg**.

## Recursos incluídos nesta base

- Escolha da versão da Bíblia.
- Tema claro/escuro.
- Menu inferior: Início, Pesquisa, Favoritos e Configurações.
- Voltar para o início.
- Pesquisa rápida.
- Favoritos.
- Criação de listas de favoritos, por exemplo `Pregação do dia 10/08/2026`.
- Marcação de texto com marca-texto.
- Zoom das letras.
- Logo/ícone de Bíblia.
- Estrutura pronta para GitHub Actions gerar APK.

## Importante sobre as traduções

Esta base não redistribui textos bíblicos protegidos por direitos autorais. Os nomes das versões podem aparecer no seletor, mas os textos completos devem ser adicionados somente quando você tiver autorização/licença para distribuí-los.

A arquitetura recomendada para produção é:

```text
app/src/main/assets/bibles/
  ara.json
  arc.json
  nvi.json
  ntlh.json
  acf.json
  naa.json
  ...
```

Formato sugerido:

```json
{
  "version": "Minha versão",
  "books": [
    {
      "name": "Gênesis",
      "chapters": [
        {
          "number": 1,
          "verses": [
            {"number": 1, "text": "Texto autorizado..."}
          ]
        }
      ]
    }
  ]
}
```

## Gerar APK no GitHub

O projeto já possui workflow em `.github/workflows/android.yml`.

Depois de enviar o projeto:

1. Abra o repositório no GitHub.
2. Entre em **Actions**.
3. Execute **Build APK**.
4. Ao terminar, abra o artefato `biblia-debug-apk`.
5. Baixe o APK para instalar no Android.

## Usar com Termux

No Termux:

```bash
pkg update -y
pkg install git openjdk-17 -y
git clone https://github.com/SEU-USUARIO/biblia-todas-versoes.git
cd biblia-todas-versoes
```

Para editar:

```bash
pkg install nano -y
nano README.md
```

Para enviar alterações:

```bash
git add .
git commit -m "Atualiza aplicativo Bíblia"
git push
```

### Build local

O Android SDK precisa estar instalado e `ANDROID_HOME` configurado. Em muitos celulares é mais simples deixar o GitHub Actions gerar o APK.

Se o Gradle estiver instalado e o SDK configurado:

```bash
gradle assembleDebug
```

O APK ficará em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Próximas melhorias recomendadas

- Banco de dados Room para favoritos, listas e marcações permanentes.
- Pesquisa indexada em todas as versões.
- Navegação por livro/capítulo/versículo.
- Importação de Bíblias em JSON.
- Sistema de notas.
- Backup/restauração.
- Compartilhar versículo como imagem.
- Leitura em voz alta.
- Sincronização opcional.
- Adicionar apenas traduções com licença adequada.
