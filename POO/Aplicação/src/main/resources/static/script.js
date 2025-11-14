// ESPERA O HTML CARREGAR ANTES DE EXECUTAR QUALQUER COISA
window.addEventListener('DOMContentLoaded', () => {

    // --- Variáveis Globais ---
    let tipoMidiaAtual = null;
    let usuarioLogado = null; 
    let midiaAtualEmDetalhe = null; 
    let idMidiaEmEdicao = null; 

    // ===============================================
    // --- 1. PEGAR TODOS OS ELEMENTOS (Com Perfil)
    // ===============================================
    const telaLogin = document.getElementById('tela-login');
    const telaCadastro = document.getElementById('tela-cadastro');
    const telaApp = document.getElementById('tela-app');
    const btnIrParaCadastro = document.getElementById('btn-ir-para-cadastro');
    const btnEntrarApp = document.getElementById('btn-entrar-app');
    const btnIrParaLogin = document.getElementById('btn-ir-para-login');
    const btnEnviarCadastro = document.getElementById('btn-enviar-cadastro');
    const cadEmailInput = document.getElementById('cad-email');
    const cadNomeInput = document.getElementById('cad-nome');
    const cadSenhaInput = document.getElementById('cad-senha');
    const cadConfirmaSenhaInput = document.getElementById('cad-confirma-senha');
    const cadNascimentoInput = document.getElementById('cad-nascimento');
    const btnLogoHome = document.getElementById('btn-logo-home');
    const btnAbrirConfig = document.getElementById('btn-abrir-config');
    const telaAtualizacoes = document.getElementById('tela-atualizacoes');
    const telaConfiguracoes = document.getElementById('tela-configuracoes');
    
    // --- Containers dos Feeds (Por Categoria) ---
    const feedFilmeContainer = document.getElementById('feed-filme');
    const feedSerieContainer = document.getElementById('feed-serie');
    const feedMusicaContainer = document.getElementById('feed-musica');
    const feedLivroContainer = document.getElementById('feed-livro');

    // --- Elementos de Perfil (REATIVADOS) ---
    const btnMenuPerfil = document.getElementById('btn-menu-perfil');
    const menuPerfil = document.getElementById('menu-perfil');
    const btnAtualizarFoto = document.getElementById('btn-atualizar-foto');
    const modalEditarPerfil = document.getElementById('modal-editar-perfil');
    const btnFecharModalFoto = document.getElementById('btn-fechar-modal-foto');
    const btnSalvarFoto = document.getElementById('btn-salvar-foto');
    const inputFotoUrl = document.getElementById('edit-foto-url');
    const headerAvatarImg = document.getElementById('header-avatar-img');
    const profilePicBg = document.getElementById('profile-pic-bg');
    const profileUserInfo = document.getElementById('profile-user-info');


    // --- Modais Funcionais ---
    const btnAbrirModalApagar = document.getElementById('btn-abrir-modal-apagar');
    const modalApagarConta = document.getElementById('modal-apagar-conta');
    const btnFecharModalApagar = document.getElementById('btn-fechar-modal-apagar');
    const btnConfirmarApagar = document.getElementById('btn-confirmar-apagar');
    const togglePrivacyInput = document.getElementById('toggle-privacy');
    const privacyToggleLabel = document.querySelector('.privacy-toggle .privacy-text');
    const privacyToggleIcon = document.querySelector('.privacy-toggle .icon-lock');
    const btnAbrirTipoMidia = document.getElementById('btn-abrir-tipo-midia');
    const modalTipoMidia = document.getElementById('modal-tipo-midia');
    const btnFecharTipoMidia = document.getElementById('btn-fechar-tipo-midia');
    const botoesTipoMidia = document.querySelectorAll('.btn-tipo-midia');
    const modalAddMidia = document.getElementById('modal-add-midia');
    const btnFecharAddMidia = document.getElementById('btn-fechar-add-midia');
    const btnSalvarMidia = document.getElementById('btn-salvar-midia');
    const addMidiaTitle = document.getElementById('add-midia-title');
    const camposDinamicos = document.querySelectorAll('.add-midia-form .form-group[data-tipo]');
    const addMidiaForm = document.querySelector('.add-midia-form');
    const stars = document.querySelectorAll('.star-rating .star');
    const midiaNotaInput = document.getElementById('midia-nota');
    const midiaDataEstreiaInput = document.getElementById('midia-data-estreia');
    
    // --- Inputs do Formulário (referências) ---
    const midiaNomeInput = document.getElementById('midia-nome');
    const midiaComentariosInput = document.getElementById('midia-comentarios');
    const midiaArtistaInput = document.getElementById('midia-artista');
    const midiaAlbumInput = document.getElementById('midia-album');
    const midiaDiretorInput = document.getElementById('midia-diretor');
    const midiaAnoLancamentoInput = document.getElementById('midia-ano-lancamento');
    const midiaGeneroSerieInput = document.getElementById('midia-genero-serie'); 
    const midiaAutorInput = document.getElementById('midia-autor');
    const midiaGeneroLivroInput = document.getElementById('midia-genero-livro'); 

    
    // --- Elementos do Modal de Detalhes ---
    const modalDetalhesMidia = document.getElementById('modal-detalhes-midia');
    const btnFecharDetalhesMidia = document.getElementById('btn-fechar-detalhes-midia');
    const btnDeletarMidia = document.getElementById('btn-deletar-midia');
    const btnEditarMidia = document.getElementById('btn-editar-midia'); 
    const detalheTitulo = document.getElementById('detalhe-titulo');
    const detalhePoster = document.getElementById('detalhe-poster');
    const detalheSinopse = document.getElementById('detalhe-sinopse');
    const detalheInfoExtra = document.getElementById('detalhe-info-extra');
    const detalheAvaliacoesLista = document.getElementById('detalhe-avaliacoes-lista');


    // ===============================================
    // --- 2. FUNÇÕES DE AJUDA (Helpers)
    // ===============================================
    
    function formatarDataParaAPI(data) {
        if (!data) return null;
        const partes = data.split('/');
        if (partes.length !== 3 || partes[2].length < 4) { return null; }
        return `${partes[2]}-${partes[1]}-${partes[0]}`;
    }

    function formatarDataParaForm(data) {
        if (!data) return "";
        try {
            const dataObj = new Date(data + 'T00:00:00'); 
            const dia = String(dataObj.getUTCDate()).padStart(2, '0');
            const mes = String(dataObj.getUTCMonth() + 1).padStart(2, '0');
            const ano = dataObj.getUTCFullYear();
            if (isNaN(dia)) return "";
            return `${dia}/${mes}/${ano}`;
        } catch(e) {
            return "";
        }
    }

    function aplicarMascaraData(e) {
        let valor = e.target.value.replace(/\D/g, ''); 
        if (valor.length > 4) {
            valor = valor.slice(0, 2) + '/' + valor.slice(2, 4) + '/' + valor.slice(4, 8);
        } else if (valor.length > 2) {
            valor = valor.slice(0, 2) + '/' + valor.slice(2, 4);
        }
        e.target.value = valor;
    }

    // --- NOVO: Atualiza a foto de perfil na UI ---
    function atualizarImagensDePerfil(fotoUrl) {
        const urlPadraoHeader = "https://i.imgur.com/4z1ZJ8H.png";
        const urlPadraoFundo = "https://i.imgur.com/I5b6nJg.png";
        
        if(headerAvatarImg) {
            headerAvatarImg.src = fotoUrl || urlPadraoHeader;
        }
        if(profilePicBg) {
            profilePicBg.style.backgroundImage = `url('${fotoUrl || urlPadraoFundo}')`;
        }
    }


    function renderizarMidias(midias, avaliacoes) {
        const containers = {
            FILME: feedFilmeContainer,
            SERIE: feedSerieContainer,
            MUSICA: feedMusicaContainer,
            LIVRO: feedLivroContainer
        };
        Object.values(containers).forEach(c => { if(c) c.innerHTML = ''; });
        
        if (midias.length === 0) {
             if (feedFilmeContainer) feedFilmeContainer.innerHTML = '<p>Você ainda não cadastrou nenhuma mídia. Clique no botão + para começar!</p>';
            return;
        }

        const mapaAvaliacoes = new Map();
        avaliacoes.forEach(av => {
            if (!mapaAvaliacoes.has(av.midiaId) || new Date(av.dataAvaliacao) > new Date(mapaAvaliacoes.get(av.midiaId).dataAvaliacao)) {
                mapaAvaliacoes.set(av.midiaId, av);
            }
        });

        midias.forEach(midia => {
            const avaliacao = mapaAvaliacoes.get(midia.id);
            const nota = avaliacao ? avaliacao.nota : 0;
            const comentario = avaliacao ? avaliacao.comentario : "Nenhuma avaliação.";
            const imagemUrl = midia.posterUrl 
                ? midia.posterUrl 
                : `https://via.placeholder.com/60x60/0d3d82/FFFFFF?text=${midia.tipo.substring(0,1)}`;

            const card = document.createElement('div');
            card.className = 'update-card-item card-clicavel';
            card.dataset.id = midia.id; 
            
            card.innerHTML = `
                <img src="${imagemUrl}" alt="${midia.nome}" class="update-card-image">
                <div class="update-card-info">
                    <span class="update-card-title">${midia.nome}</span>
                    <span class="update-card-desc">${comentario.substring(0, 50)}...</span>
                    <div class="update-card-stars">${'★'.repeat(nota)}${'☆'.repeat(5 - nota)}</div>
                </div>
                <div class="update-card-date">
                    <span>ID</span>
                    <span>${midia.id}</span>
                </div>
            `;
            
            const container = containers[midia.tipo];
            if (container) {
                container.appendChild(card);
            }
        });
    }

    async function carregarFeedUsuario() {
        try {
            const [midiasResponse, avaliacoesResponse] = await Promise.all([
                fetch('/api/midias/minhas-midias', { credentials: 'include' }),
                fetch('/api/avaliacoes/minhas-avaliacoes', { credentials: 'include' })
            ]);
            if (!midiasResponse.ok || !avaliacoesResponse.ok) {
                if (midiasResponse.status === 401 || avaliacoesResponse.status === 401) {
                    throw new Error('Sessão expirada. Faça login novamente.');
                }
                throw new Error('Erro ao carregar seu feed.');
            }
            const midias = await midiasResponse.json();
            const avaliacoes = await avaliacoesResponse.json();
            renderizarMidias(midias, avaliacoes);
        } catch (error) {
            console.error("Erro ao carregar feed:", error);
            if(feedFilmeContainer) feedFilmeContainer.innerHTML = `<p style="color: red;">${error.message}</p>`;
            if (error.message.includes("Sessão expirada")) {
                telaApp.classList.add('escondido');
                telaLogin.classList.remove('escondido');
            }
        }
    }

    function preencherModalDetalhes(midia, avaliacoes) {
        midiaAtualEmDetalhe = midia; 
        detalheTitulo.textContent = midia.nome;
        detalhePoster.src = midia.posterUrl || `https://via.placeholder.com/200x300/004a99/FFFFFF?text=${midia.nome}`;
        detalheSinopse.textContent = midia.sinopse || "Esta mídia não tem sinopse.";
        let infoExtraHtml = '';
        if (midia.tipo === 'FILME') {
            infoExtraHtml = `<strong>Diretor:</strong> ${midia.diretor || 'N/A'}<br><strong>Ano:</strong> ${midia.anoLancamento || 'N/A'}`;
        } else if (midia.tipo === 'SERIE') {
            infoExtraHtml = `<strong>Gênero:</strong> ${midia.genero || 'N/A'}`;
        } else if (midia.tipo === 'MUSICA') {
            infoExtraHtml = `<strong>Artista:</strong> ${midia.artista || 'N/A'}<br><strong>Álbum:</strong> ${midia.album || 'N/A'}<br><strong>Estreia:</strong> ${formatarDataParaForm(midia.dataEstreia) || 'N/A'}`;
        } else if (midia.tipo === 'LIVRO') {
            infoExtraHtml = `<strong>Autor:</strong> ${midia.autor || 'N/A'}<br><strong>Gênero:</strong> ${midia.genero || 'N/A'}`;
        }
        detalheInfoExtra.innerHTML = infoExtraHtml;

        detalheAvaliacoesLista.innerHTML = '';
        if (avaliacoes.length > 0) {
            avaliacoes.sort((a, b) => new Date(b.dataAvaliacao) - new Date(a.dataAvaliacao));
            avaliacoes.forEach(av => {
                const avaliacaoCard = document.createElement('div');
                avaliacaoCard.className = 'avaliacao-item';
                avaliacaoCard.innerHTML = `
                    <div class="avaliacao-item-header">
                        <span class="avaliacao-item-usuario">Minha Avaliação (${formatarDataParaForm(av.dataAvaliacao)})</span>
                        <span class="avaliacao-item-nota">${'★'.repeat(av.nota)}${'☆'.repeat(5 - av.nota)}</span>
                    </div>
                    <p class="avaliacao-item-comentario">${av.comentario || '<i>Sem comentário</i>'}</p>
                `;
                detalheAvaliacoesLista.appendChild(avaliacaoCard);
            });
        } else {
            detalheAvaliacoesLista.innerHTML = '<p>Nenhuma avaliação encontrada para esta mídia.</p>';
        }
    }

    async function abrirModalDetalhes(midiaId) {
        midiaAtualEmDetalhe = null;
        modalDetalhesMidia.classList.remove('escondido');
        detalheTitulo.textContent = 'Carregando...';
        detalhePoster.src = 'https://via.placeholder.com/200x300/004a99/FFFFFF?text=...';
        detalheSinopse.textContent = 'Carregando sinopse...';
        detalheInfoExtra.innerHTML = '';
        detalheAvaliacoesLista.innerHTML = '<p>Carregando avaliações...</p>';
        
        try {
            const [midiaResponse, avaliacoesResponse] = await Promise.all([
                fetch(`/api/midias/${midiaId}`, { credentials: 'include' }),
                fetch(`/api/avaliacoes/midia/${midiaId}`, { credentials: 'include' })
            ]);
            if (midiaResponse.status === 401 || avaliacoesResponse.status === 401) {
                 throw new Error('Sessão expirada. Faça login novamente.');
            }
            if (!midiaResponse.ok || !avaliacoesResponse.ok) {
                throw new Error('Erro ao buscar detalhes da mídia.');
            }
            const midia = await midiaResponse.json();
            const avaliacoes = await avaliacoesResponse.json();
            preencherModalDetalhes(midia, avaliacoes);
        } catch (error) {
            console.error("Erro ao abrir detalhes:", error);
            alert(error.message);
            modalDetalhesMidia.classList.add('escondido'); 
            if (error.message.includes("Sessão expirada")) {
                telaApp.classList.add('escondido');
                telaLogin.classList.remove('escondido');
            }
        }
    }

    // ===============================================
    // --- 3. LÓGICA DE NAVEGAÇÃO E API
    // ===============================================

    // --- Navegação (Login / Cadastro)
    btnIrParaCadastro.addEventListener('click', () => { telaLogin.classList.add('escondido'); telaCadastro.classList.remove('escondido'); });
    btnIrParaLogin.addEventListener('click', () => { telaCadastro.classList.add('escondido'); telaLogin.classList.remove('escondido'); });
    if(cadNascimentoInput) { cadNascimentoInput.addEventListener('input', aplicarMascaraData); }
    if (midiaDataEstreiaInput) { midiaDataEstreiaInput.addEventListener('input', aplicarMascaraData); }

    // --- API: Botão ENVIAR CADASTRO
    btnEnviarCadastro.addEventListener('click', () => {
        const email = cadEmailInput.value; const nome = cadNomeInput.value; const senha = cadSenhaInput.value;
        const senhaConfirmacao = cadConfirmaSenhaInput.value; const dataNascimentoInput = cadNascimentoInput.value;
        if (!email || !nome || !senha || !senhaConfirmacao || !dataNascimentoInput) { alert("Por favor, preencha todos os campos."); return; }
        if (senha !== senhaConfirmacao) { alert("As senhas não conferem!"); return; }
        const dataNascimentoFormatada = formatarDataParaAPI(dataNascimentoInput);
        if (!dataNascimentoFormatada) { alert("Formato de data inválido. Por favor, use DD/MM/AAAA."); return; }
        const dadosCadastro = { nome: nome, email: email, senha: senha, dataNascimento: dataNascimentoFormatada };
        fetch('/api/usuarios/cadastro', { method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(dadosCadastro) })
        .then(response => {
            if (!response.ok) { return response.text().then(text => { if (text.includes("Email já cadastrado")) { throw new Error('Este email já está em uso.'); } throw new Error('Erro ao cadastrar: ' + text); }); }
            return response.json(); 
        })
        .then(usuarioSalvo => {
            alert('Usuário cadastrado com sucesso! ID: ' + usuarioSalvo.id);
            cadEmailInput.value = ''; cadNomeInput.value = ''; cadSenhaInput.value = ''; cadConfirmaSenhaInput.value = ''; cadNascimentoInput.value = '';
            telaCadastro.classList.add('escondido'); telaLogin.classList.remove('escondido');
        })
        .catch(error => { console.error(error); alert(error.message); });
    });

    // --- API: Botão ENTRAR (Login) ---
    btnEntrarApp.addEventListener('click', () => {
        const email = document.getElementById('login-email').value;
        const senha = document.getElementById('login-senha').value;
        if (email === "" || senha === "") { alert('Por favor, digite seu e-mail e senha.'); return; }
        const dadosLogin = { email: email, senha: senha };
        fetch('/api/usuarios/login', { method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(dadosLogin) })
        .then(response => {
            if (response.status === 401) { throw new Error('E-mail ou senha inválidos.'); }
            if (!response.ok) { throw new Error('Erro ao tentar fazer login.'); }
            return response.json();
        })
        .then(usuarioLogadoDTO => {
            alert('Login bem-sucedido! Bem-vindo(a), ' + usuarioLogadoDTO.nome);
            usuarioLogado = usuarioLogadoDTO; 
            document.getElementById('login-email').value = ''; document.getElementById('login-senha').value = '';
            telaLogin.classList.add('escondido'); telaCadastro.classList.add('escondido'); telaApp.classList.remove('escondido');
            
            // ATUALIZA A UI COM OS DADOS DO UTILIZADOR
            profileUserInfo.textContent = usuarioLogado.nome;
            atualizarImagensDePerfil(usuarioLogado.fotoUrl); // <-- ATUALIZA A FOTO

            esconderTelasApp();
            telaAtualizacoes.classList.remove('escondido');
            carregarFeedUsuario();
        })
        .catch(error => { alert(error.message); });
    });
    
    // --- Navegação (Dentro do App)
    function esconderTelasApp() {
        if(telaAtualizacoes) telaAtualizacoes.classList.add('escondido');
        if(telaConfiguracoes) telaConfiguracoes.classList.add('escondido');
    }
    if(btnLogoHome) { btnLogoHome.addEventListener('click', () => { esconderTelasApp(); telaAtualizacoes.classList.remove('escondido'); carregarFeedUsuario(); }); }
    if(btnAbrirConfig) { btnAbrirConfig.addEventListener('click', () => { esconderTelasApp(); telaConfiguracoes.classList.remove('escondido'); }); }
    
    // Click listener genérico (simplificado)
    document.addEventListener('click', (e) => {
        // Fecha o menu de perfil se clicar fora
        if(menuPerfil && !menuPerfil.contains(e.target) && e.target !== btnMenuPerfil) {
             menuPerfil.classList.add('escondido'); 
        }
    });

    // --- Listener de clique para ABRIR o modal de detalhes ---
    [feedFilmeContainer, feedSerieContainer, feedMusicaContainer, feedLivroContainer].forEach(container => {
        if (container) {
            container.addEventListener('click', (e) => {
                const card = e.target.closest('.card-clicavel');
                if (card && card.dataset.id) {
                    abrirModalDetalhes(card.dataset.id);
                }
            });
        }
    });

    // --- Listener de clique para FECHAR o modal de detalhes ---
    if (btnFecharDetalhesMidia) {
        btnFecharDetalhesMidia.addEventListener('click', () => {
            modalDetalhesMidia.classList.add('escondido');
        });
    }

    // --- API: Botão DELETAR MÍDIA ---
    if (btnDeletarMidia) {
        btnDeletarMidia.addEventListener('click', () => {
            if (!midiaAtualEmDetalhe) return;
            const idParaDeletar = midiaAtualEmDetalhe.id;
            if (!confirm(`Tem certeza que quer deletar a mídia ID ${idParaDeletar}? Esta ação não pode ser desfeita.`)) {
                return;
            }
            fetch(`/api/midias/${idParaDeletar}`, {
                method: 'DELETE',
                credentials: 'include'
            })
            .then(response => {
                if (response.status === 401 || response.status === 403) { throw new Error('Erro de autenticação ou permissão.'); }
                if (response.status === 404) { throw new Error('Mídia não encontrada.'); }
                if (response.status === 500) { throw new Error('Erro interno do servidor. Verifique se esta mídia não está em uso.'); }
                if (!response.ok) { throw new Error('Erro ao deletar a mídia.'); }
                alert('Mídia deletada com sucesso!');
                modalDetalhesMidia.classList.add('escondido');
                carregarFeedUsuario(); 
            })
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
        });
    }

    // --- API: Botão EDITAR MÍDIA ---
    if (btnEditarMidia) {
        btnEditarMidia.addEventListener('click', () => {
            if (!midiaAtualEmDetalhe) {
                alert("Erro: Mídia não carregada.");
                return;
            }

            idMidiaEmEdicao = midiaAtualEmDetalhe.id;
            const tipo = midiaAtualEmDetalhe.tipo.toLowerCase();
            tipoMidiaAtual = tipo; 
            modalDetalhesMidia.classList.add('escondido');
            const nomeBotao = document.querySelector(`.btn-tipo-midia[data-tipo="${tipo}"]`).textContent;
            abrirModalAddMidia(tipo, nomeBotao);

            midiaNomeInput.value = midiaAtualEmDetalhe.nome || '';
            midiaComentariosInput.value = '';
            resetarEstrelas(); 

            if (tipo === 'filme') {
                midiaDiretorInput.value = midiaAtualEmDetalhe.diretor || '';
                midiaAnoLancamentoInput.value = midiaAtualEmDetalhe.anoLancamento || '';
            } else if (tipo === 'serie') {
                midiaGeneroSerieInput.value = midiaAtualEmDetalhe.genero || '';
            } else if (tipo === 'musica') {
                midiaArtistaInput.value = midiaAtualEmDetalhe.artista || '';
                midiaAlbumInput.value = midiaAtualEmDetalhe.album || '';
                midiaDataEstreiaInput.value = formatarDataParaForm(midiaAtualEmDetalhe.dataEstreia);
            } else if (tipo === 'livro') {
                midiaAutorInput.value = midiaAtualEmDetalhe.autor || '';
                midiaGeneroLivroInput.value = midiaAtualEmDetalhe.genero || '';
            }
        });
    }

    // --- LÓGICA DE PERFIL (REATIVADA) ---
    if(btnMenuPerfil) { 
        btnMenuPerfil.addEventListener('click', (e) => { 
            e.stopPropagation(); 
            menuPerfil.classList.toggle('escondido'); 
        }); 
    }
    if(btnAtualizarFoto) { 
        btnAtualizarFoto.addEventListener('click', () => { 
            menuPerfil.classList.add('escondido'); 
            inputFotoUrl.value = usuarioLogado.fotoUrl || ''; // Preenche com a URL atual
            modalEditarPerfil.classList.remove('escondido'); 
        }); 
    }
    if(btnFecharModalFoto) { 
        btnFecharModalFoto.addEventListener('click', () => { 
            modalEditarPerfil.classList.add('escondido'); 
        }); 
    }
    if(btnSalvarFoto) { 
        btnSalvarFoto.addEventListener('click', () => { 
            const novaFotoUrl = inputFotoUrl.value;
            if (!novaFotoUrl || !novaFotoUrl.startsWith('http')) {
                alert("Por favor, insira um URL válido (começando com http:// ou https://).");
                return;
            }

            fetch('/api/usuarios/atualizar-foto', {
                method: 'POST',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fotoUrl: novaFotoUrl })
            })
            .then(response => {
                if (response.status === 401) { throw new Error('Sessão expirada.'); }
                if (!response.ok) { throw new Error('Não foi possível atualizar a foto.'); }
                
                alert('Foto atualizada com sucesso!');
                modalEditarPerfil.classList.add('escondido');
                
                // Atualiza a foto na UI e no objeto 'usuarioLogado'
                usuarioLogado.fotoUrl = novaFotoUrl;
                atualizarImagensDePerfil(novaFotoUrl);
            })
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
        }); 
    }

    // --- Lógica (Configurações e Apagar Conta)
    if(togglePrivacyInput) {
        togglePrivacyInput.addEventListener('change', () => {
            if (togglePrivacyInput.checked) {
                privacyToggleLabel.textContent = 'PÚBLICO'; privacyToggleIcon.textContent = '🔓';
            } else {
                privacyToggleLabel.textContent = 'PRIVADO'; privacyToggleIcon.textContent = '🔒';
            }
        });
    }
    if(btnAbrirModalApagar) { btnAbrirModalApagar.addEventListener('click', () => { modalApagarConta.classList.remove('escondido'); }); }
    if(btnFecharModalApagar) { btnFecharModalApagar.addEventListener('click', () => { modalApagarConta.classList.add('escondido'); }); }
    if(btnConfirmarApagar) {
        btnConfirmarApagar.addEventListener('click', () => {
            // TODO: Implementar chamada de API para apagar conta
            modalApagarConta.classList.add('escondido');
            alert('Conta apagada com sucesso! (Fictício)');
            telaApp.classList.add('escondido');
            telaLogin.classList.remove('escondido');
        });
    }

    // --- Lógica (Adicionar Mídia - Botão '+')
    if(btnAbrirTipoMidia) { 
        btnAbrirTipoMidia.addEventListener('click', () => { 
            idMidiaEmEdicao = null; 
            tipoMidiaAtual = null; 
            modalTipoMidia.classList.remove('escondido'); 
        }); 
    }
    if(btnFecharTipoMidia) { btnFecharTipoMidia.addEventListener('click', () => { modalTipoMidia.classList.add('escondido'); }); }

    botoesTipoMidia.forEach(botao => {
        botao.addEventListener('click', () => {
            tipoMidiaAtual = botao.dataset.tipo; 
            if (tipoMidiaAtual === 'nova') {
                alert('Função "Nova Categoria" não implementada.'); return;
            }
            abrirModalAddMidia(tipoMidiaAtual, botao.textContent.trim());
        });
    });

    function abrirModalAddMidia(tipo, nomeTipo) {
        modalTipoMidia.classList.add('escondido');
        const icones = { 'musica': '🎵', 'filme': '🎬', 'serie': '📺', 'livro': '📖' };
        
        if (idMidiaEmEdicao) {
            addMidiaTitle.textContent = `📝 Editando ${nomeTipo}`;
        } else {
            addMidiaTitle.textContent = `${icones[tipo] || '📝'} Novo(a) ${nomeTipo}`;
        }
        
        camposDinamicos.forEach(campo => { campo.style.display = 'none'; });
        const camposParaMostrar = document.querySelectorAll(`.form-group[data-tipo="${tipo}"]`);
        camposParaMostrar.forEach(campo => { campo.style.display = 'block'; });
        
        if (!idMidiaEmEdicao) {
            if(addMidiaForm) addMidiaForm.reset(); 
            resetarEstrelas(); 
        }
        
        modalAddMidia.classList.remove('escondido');
    }

    stars.forEach(star => {
        star.addEventListener('click', () => {
            const valor = star.dataset.value;
            midiaNotaInput.value = valor;
            stars.forEach(s => {
                s.textContent = (s.dataset.value <= valor) ? '★' : '☆';
            });
        });
    });

    function resetarEstrelas() {
        stars.forEach(s => s.textContent = '☆');
        midiaNotaInput.value = "0";
    }

    if(btnFecharAddMidia) { 
        btnFecharAddMidia.addEventListener('click', () => { 
            modalAddMidia.classList.add('escondido'); 
            idMidiaEmEdicao = null; 
            tipoMidiaAtual = null;
        }); 
    }

    // --- API: Auto-fill (só funciona se for mídia NOVA) ---
    if(midiaNomeInput) {
        midiaNomeInput.addEventListener('blur', () => { 
            if (idMidiaEmEdicao) return; 
            const nome = midiaNomeInput.value;
            if (nome.length < 3 || (tipoMidiaAtual !== 'filme' && tipoMidiaAtual !== 'serie')) {
                return; 
            }
            
            if (tipoMidiaAtual === 'filme') {
                 if (midiaAnoLancamentoInput) midiaAnoLancamentoInput.value = '';
                 if (midiaDiretorInput) midiaDiretorInput.value = '';
            }
            if (tipoMidiaAtual === 'serie' && midiaGeneroSerieInput) midiaGeneroSerieInput.value = '';
            
            fetch(`/api/tmdb/buscar?query=${nome}&tipo=${tipoMidiaAtual}`, { credentials: 'include' })
                .then(response => {
                    if (!response.ok) { throw new Error('Mídia não encontrada.'); }
                    return response.json();
                })
                .then(data => {
                    if (tipoMidiaAtual === 'filme') {
                        if(midiaAnoLancamentoInput) midiaAnoLancamentoInput.value = data.anoLancamento || '';
                        if(midiaDiretorInput) midiaDiretorInput.value = data.diretor || '';
                    } else if (tipoMidiaAtual === 'serie') {
                        if(midiaGeneroSerieInput) midiaGeneroSerieInput.value = data.genero || '';
                    }
                })
                .catch(error => {
                    console.warn(error.message);
                });
        });
    }

    // --- ATUALIZADO: API: Botão SALVAR MÍDIA (Criação ou Edição) ---
    if(btnSalvarMidia) {
        btnSalvarMidia.addEventListener('click', () => {
            if (!tipoMidiaAtual) { 
                alert("Erro: Tipo de mídia desconhecido."); 
                return; 
            }
            
            let url = `/api/midias/${tipoMidiaAtual}`;
            let metodo = 'POST'; 

            if (idMidiaEmEdicao) {
                url = `/api/midias/${tipoMidiaAtual}/${idMidiaEmEdicao}`;
                metodo = 'PUT';
            }
            
            let payload = {};
            const nome = document.getElementById('midia-nome').value;
            const nota = parseInt(midiaNotaInput.value, 10);
            const comentario = document.getElementById('midia-comentarios').value;
            if (nome === "") { alert('O campo "Nome" é obrigatório!'); return; }
            payload.nome = nome; payload.nota = nota; payload.comentario = comentario;

            if (tipoMidiaAtual === 'filme') {
                payload.diretor = document.getElementById('midia-diretor').value;
                payload.anoLancamento = parseInt(document.getElementById('midia-ano-lancamento').value, 10) || 0; 
            } else if (tipoMidiaAtual === 'serie') {
                payload.genero = document.getElementById('midia-genero-serie').value;
            } else if (tipoMidiaAtual === 'musica') {
                payload.artista = document.getElementById('midia-artista').value;
                payload.album = document.getElementById('midia-album').value;
                payload.dataEstreia = formatarDataParaAPI(document.getElementById('midia-data-estreia').value);
            } else if (tipoMidiaAtual === 'livro') {
                payload.autor = document.getElementById('midia-autor').value;
                payload.genero = document.getElementById('midia-genero-livro').value; 
            }
            
            fetch(url, {
                method: metodo, 
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (response.status === 401 || response.status === 403) { throw new Error('Erro de autenticação. Faça login novamente.'); }
                if (!response.ok) { return response.text().then(text => { throw new Error('Erro ao salvar mídia: ' + text) }); }
                return response.json();
            })
            .then(midiaSalva => {
                if (idMidiaEmEdicao) {
                    alert('Mídia atualizada com sucesso! ID: ' + midiaSalva.id);
                } else {
                    alert('Mídia salva com sucesso! ID: ' + midiaSalva.id);
                }
                
                modalAddMidia.classList.add('escondido');
                idMidiaEmEdicao = null; 
                tipoMidiaAtual = null;
                carregarFeedUsuario(); 
            })
            .catch(error => { 
                console.error(error); 
                alert(error.message); 
            });
        });
    }

}); // Fim do 'DOMContentLoaded'