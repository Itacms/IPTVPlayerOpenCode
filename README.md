# TCL Stream Clone (com.example.tclstreamclone)

Projeto Android (Kotlin + Jetpack Compose) inspirado no TCL Stream, com aba **Playlists** para adicionar listas M3U/M3U8 (URL ou arquivo local) e reprodução via ExoPlayer (Media3).

## Funcionalidades
- Aba Início, Playlists e EPG (bottom navigation).
- Adicionar lista por URL (download + parse M3U/M3U8) ou arquivo local.
- Busca de canais e filtros por categoria (group-title) + Favoritos.
- Reprodução (HLS/M3U8) com ExoPlayer + controles e BackHandler.
- EPG básico (XMLTV) com filtro e programa atual por canal.
- Persistência das listas e tema via DataStore (JSON).
- Tema claro/escuro persistente (Material You no Android 12+).

## Prioridades implementadas (ordem)
1. Tema (claro/escuro persistente)
2. Categorias (chips + sticky headers por group-title)
3. Busca (filtro por nome)
4. EPG (XMLTV básico + programa atual por canal + overlay no player)
5. Favoritos (toggle + filtro)
6. Layout TV (foco D-pad, BackHandler, navegação por abas)

## Compilar no GitHub Codespaces
1. Faça upload/extraia este .zip em um repositório e abra no Codespaces.
2. O devcontainer instala Java 17 + Android SDK 34 automaticamente.
3. Rode: `./gradlew assembleDebug`
4. O APK ficará em `app/build/outputs/apk/debug/`.

## Limitações atuais
- Sem decompilação do TCL Stream original (jadx não disponível); este é um app novo inspirado, não um clone binário.
- Parser M3U básico (EXTINF, group-title, tvg-logo). Sem EPG/XMLTV ainda.
- Sem suporte a DRM/autenticação de provedores.
- UI simplificada; adaptação TV (D-pad) básica via Compose.

## Próximos passos sugeridos
- Adicionar EPG (XMLTV) e grupos/categorias.
- Favoritos e histórico.
- Suporte a múltiplos formatos (m3u, m3u8, txt).
- Tema escuro/claro e layout leanback dedicado para TV.
