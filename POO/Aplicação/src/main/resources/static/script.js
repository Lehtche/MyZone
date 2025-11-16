// ESPERA O HTML CARREGAR ANTES DE EXECUTAR QUALQUER COISA
window.addEventListener('DOMContentLoaded', () => {

    // --- Variáveis Globais ---
    let tipoMidiaAtual = null;
    let usuarioLogado = null; 
    let midiaAtualEmDetalhe = null; 
    let avaliacaoAtualDoUsuario = null; 
    let idMidiaEmEdicao = null; 
    let apiDataSelecionada = null; 

    // ===============================================
    // --- 1. PEGAR TODOS OS ELEMENTOS
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
    
    const feedFilmeContainer = document.getElementById('feed-filme');
    const feedSerieContainer = document.getElementById('feed-serie');
    const feedMusicaContainer = document.getElementById('feed-musica');
    const feedLivroContainer = document.getElementById('feed-livro');

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
    const stars = document.querySelectorAll('.star-rating .star'); // Estrelas do modal ADICIONAR
    const midiaNotaInput = document.getElementById('midia-nota');
    // const midiaDataEstreiaInput = document.getElementById('midia-data-estreia'); // <-- REMOVIDO
    
    const midiaNomeInput = document.getElementById('midia-nome');
    const midiaComentariosInput = document.getElementById('midia-comentarios');
    const midiaArtistaInput = document.getElementById('midia-artista');
    const midiaAlbumInput = document.getElementById('midia-album');
    const midiaAnoEstreiaInput = document.getElementById('midia-ano-estreia'); // <-- ADICIONADO
    const midiaDiretorInput = document.getElementById('midia-diretor');
    const midiaAnoLancamentoInput = document.getElementById('midia-ano-lancamento');
    const midiaGeneroSerieInput = document.getElementById('midia-genero-serie'); 
    const midiaAutorInput = document.getElementById('midia-autor');
    const midiaGeneroLivroInput = document.getElementById('midia-genero-livro'); 

    const apiSearchResults = document.getElementById('api-search-results');

    const modalDetalhesMidia = document.getElementById('modal-detalhes-midia');
    const btnFecharDetalhesMidia = document.getElementById('btn-fechar-detalhes-midia');
    const btnDeletarMidia = document.getElementById('btn-deletar-midia');
    const btnEditarMidia = document.getElementById('btn-editar-midia'); 
    const detalheTitulo = document.getElementById('detalhe-titulo');
    const detalhePoster = document.getElementById('detalhe-poster');
    const detalheSinopse = document.getElementById('detalhe-sinopse');
    const detalheInfoExtra = document.getElementById('detalhe-info-extra');
    const detalheAvaliacoesLista = document.getElementById('detalhe-avaliacoes-lista');

    // === ELEMENTOS DO NOVO MODAL DE AVALIAÇÃO ===
    const modalAddAvaliacao = document.getElementById('modal-add-avaliacao');
    const btnFecharModalAvaliacao = document.getElementById('btn-fechar-modal-avaliacao');
    const btnSalvarAvaliacao = document.getElementById('btn-salvar-avaliacao');
    const btnAbrirAvaliacao = document.getElementById('btn-abrir-avaliacao');
    const avaliacaoStars = document.querySelectorAll('#avaliacao-stars .star'); // Estrelas do modal AVALIAR
    const avaliacaoNotaInput = document.getElementById('avaliacao-nota');
    const avaliacaoComentarioInput = document.getElementById('avaliacao-comentario');
    const modalAvaliacaoTitulo = document.getElementById('modal-avaliacao-titulo');


    // ===============================================
    // --- 2. FUNÇÕES DE AJUDA (Helpers)
    // ===============================================
    
    function debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            const context = this;
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
            timeoutId = setTimeout(() => {
                func.apply(context, args); 
            }, delay);
        };
    }

    function formatarDataParaAPI(data) {
        if (!data) return null;
        const partes = data.split('/');
        if (partes.length !== 3 || partes[2].length < 4) { return null; }
        return `${partes[2]}-${partes[1]}-${partes[0]}`;
    }
    function formatarDataParaForm(data) {
        if (!data) return "";
        // Não formata mais o ano (que é int), apenas datas completas (Nascimento)
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
            if (av.usuarioId === usuarioLogado.id) {
                if (!mapaAvaliacoes.has(av.midiaId) || new Date(av.dataAvaliacao) > new Date(mapaAvaliacoes.get(av.midiaId).dataAvaliacao)) {
                    mapaAvaliacoes.set(av.midiaId, av);
                }
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
    
    // === "preencherModalDetalhes" ATUALIZADO ===
    function preencherModalDetalhes(midia, avaliacoes) {
        midiaAtualEmDetalhe = midia; 
        avaliacaoAtualDoUsuario = null; 
        
        detalheTitulo.textContent = midia.nome;
        detalhePoster.src = midia.posterUrl || `https://via.placeholder.com/200x300/004a99/FFFFFF?text=${midia.nome}`;
        detalheSinopse.textContent = midia.sinopse || "Esta mídia não tem sinopse.";
        detalheSinopse.classList.remove('letra-de-musica');
        
        let infoExtraHtml = '';
        if (midia.tipo === 'FILME') {
            infoExtraHtml = `<strong>Diretor:</strong> ${midia.diretor || 'N/A'}<br><strong>Ano:</strong> ${midia.anoLancamento || 'N/A'}`;
        } else if (midia.tipo === 'SERIE') {
            infoExtraHtml = `<strong>Gênero:</strong> ${midia.genero || 'N/A'}`;
        } else if (midia.tipo === 'MUSICA') {
            detalheSinopse.classList.add('letra-de-musica');
            // --- CAMPO ATUALIZADO ---
            infoExtraHtml = `<strong>Artista:</strong> ${midia.artista || 'N/A'}<br><strong>Álbum:</strong> ${midia.album || 'N/A'}<br><strong>Ano:</strong> ${midia.anoEstreia || 'N/A'}`;
        } else if (midia.tipo === 'LIVRO') {
            infoExtraHtml = `<strong>Autor:</strong> ${midia.autor || 'N/A'}<br><strong>Gênero:</strong> ${midia.genero || 'N/A'}`;
        }
        detalheInfoExtra.innerHTML = infoExtraHtml;
        
        detalheAvaliacoesLista.innerHTML = '';
        
        const minhaAvaliacao = avaliacoes.find(av => av.usuarioId === usuarioLogado.id);
        
        if (minhaAvaliacao) {
            avaliacaoAtualDoUsuario = minhaAvaliacao; 
            const avaliacaoCard = document.createElement('div');
            avaliacaoCard.className = 'avaliacao-item';
            avaliacaoCard.style.border = "2px solid #0056b3"; 
            avaliacaoCard.innerHTML = `
                <div class="avaliacao-item-header">
                    <span class="avaliacao-item-usuario" style="color: #0056b3;">Minha Avaliação (${formatarDataParaForm(minhaAvaliacao.dataAvaliacao)})</span>
                    <span class="avaliacao-item-nota">${'★'.repeat(minhaAvaliacao.nota)}${'☆'.repeat(5 - minhaAvaliacao.nota)}</span>
                </div>
                <p class="avaliacao-item-comentario">${minhaAvaliacao.comentario || '<i>Sem comentário</i>'}</p>
            `;
            detalheAvaliacoesLista.appendChild(avaliacaoCard);
        }

        avaliacoes.forEach(av => {
            if (av.usuarioId !== usuarioLogado.id) { 
                const avaliacaoCard = document.createElement('div');
                avaliacaoCard.className = 'avaliacao-item';
                // (Opcional: podemos buscar o nome do usuário futuramente usando a View VW_Usuarios_Publicos)
                avaliacaoCard.innerHTML = `
                    <div class="avaliacao-item-header">
                        <span class="avaliacao-item-usuario">Avaliação (ID Utilizador: ${av.usuarioId})</span>
                        <span class="avaliacao-item-nota">${'★'.repeat(av.nota)}${'☆'.repeat(5 - av.nota)}</span>
                    </div>
                    <p class="avaliacao-item-comentario">${av.comentario || '<i>Sem comentário</i>'}</p>
                `;
                detalheAvaliacoesLista.appendChild(avaliacaoCard);
            }
        });

        if (detalheAvaliacoesLista.innerHTML === '') {
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

    // (Login, Cadastro, Navegação)
    btnIrParaCadastro.addEventListener('click', () => { telaLogin.classList.add('escondido'); telaCadastro.classList.remove('escondido'); });
    btnIrParaLogin.addEventListener('click', () => { telaCadastro.classList.add('escondido'); telaLogin.classList.remove('escondido'); });
    
    // Aplica máscara apenas ao campo de nascimento
    if(cadNascimentoInput) { cadNascimentoInput.addEventListener('input', aplicarMascaraData); }
    // if (midiaDataEstreiaInput) { midiaDataEstreiaInput.addEventListener('input', aplicarMascaraData); } // <-- REMOVIDO

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
            profileUserInfo.textContent = usuarioLogado.nome;
            atualizarImagensDePerfil(usuarioLogado.fotoUrl); 
            esconderTelasApp();
            telaAtualizacoes.classList.remove('escondido');
            carregarFeedUsuario();
        })
        .catch(error => { alert(error.message); });
    });
    function esconderTelasApp() {
        if(telaAtualizacoes) telaAtualizacoes.classList.add('escondido');
        if(telaConfiguracoes) telaConfiguracoes.classList.add('escondido');
    }
    if(headerAvatarImg) { headerAvatarImg.addEventListener('click', () => { esconderTelasApp(); telaAtualizacoes.classList.remove('escondido'); carregarFeedUsuario(); }); }
    if(btnLogoHome) { btnLogoHome.addEventListener('click', () => { esconderTelasApp(); telaAtualizacoes.classList.remove('escondido'); carregarFeedUsuario(); }); }
    if(btnAbrirConfig) { btnAbrirConfig.addEventListener('click', () => { esconderTelasApp(); telaConfiguracoes.classList.remove('escondido'); }); }
    document.addEventListener('click', (e) => {
        if(menuPerfil && !menuPerfil.contains(e.target) && e.target !== btnMenuPerfil) {
             menuPerfil.classList.add('escondido'); 
        }
    });
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
    if (btnFecharDetalhesMidia) {
        btnFecharDetalhesMidia.addEventListener('click', () => {
            modalDetalhesMidia.classList.add('escondido');
        });
    }
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
    
    // === "btnEditarMidia" ATUALIZADO ===
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
            
            if (avaliacaoAtualDoUsuario) {
                midiaComentariosInput.value = avaliacaoAtualDoUsuario.comentario || '';
                setStars(avaliacaoAtualDoUsuario.nota); 
            } else {
                midiaComentariosInput.value = '';
                resetarEstrelas(); 
            }
            
            if (tipo === 'filme') {
                midiaDiretorInput.value = midiaAtualEmDetalhe.diretor || '';
                midiaAnoLancamentoInput.value = midiaAtualEmDetalhe.anoLancamento || '';
            } else if (tipo === 'serie') {
                midiaGeneroSerieInput.value = midiaAtualEmDetalhe.genero || '';
            } else if (tipo === 'musica') {
                midiaArtistaInput.value = midiaAtualEmDetalhe.artista || '';
                midiaAlbumInput.value = midiaAtualEmDetalhe.album || '';
                // --- CAMPO ATUALIZADO ---
                midiaAnoEstreiaInput.value = midiaAtualEmDetalhe.anoEstreia || '';
            } else if (tipo === 'livro') {
                midiaAutorInput.value = midiaAtualEmDetalhe.autor || '';
                midiaGeneroLivroInput.value = midiaAtualEmDetalhe.genero || '';
            }
        });
    }
    if(btnMenuPerfil) { 
        btnMenuPerfil.addEventListener('click', (e) => { 
            e.stopPropagation(); 
            menuPerfil.classList.toggle('escondido'); 
        }); 
    }
    if(btnAtualizarFoto) { 
        btnAtualizarFoto.addEventListener('click', () => { 
            menuPerfil.classList.add('escondido'); 
            inputFotoUrl.value = usuarioLogado.fotoUrl || '';
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
            if (novaFotoUrl && !novaFotoUrl.startsWith('http')) {
                alert("Por favor, insira um URL válido (começando com http:// ou https://) ou deixe em branco.");
                return;
            }
            const urlParaSalvar = novaFotoUrl || null; 
            fetch('/api/usuarios/atualizar-foto', {
                method: 'POST',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fotoUrl: urlParaSalvar })
            })
            .then(response => {
                if (response.status === 401) { throw new Error('Sessão expirada.'); }
                if (!response.ok) { throw new Error('Não foi possível atualizar a foto.'); }
                alert('Foto atualizada com sucesso!');
                modalEditarPerfil.classList.add('escondido');
                usuarioLogado.fotoUrl = urlParaSalvar;
                atualizarImagensDePerfil(urlParaSalvar);
            })
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
        }); 
    }
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
            modalApagarConta.classList.add('escondido');
            alert('Conta apagada com sucesso! (Fictício)');
            telaApp.classList.add('escondido');
            telaLogin.classList.remove('escondido');
        });
    }

    // --- Lógica (Adicionar Mídia - Botão '+') ---
    
    if(btnAbrirTipoMidia) { 
        btnAbrirTipoMidia.addEventListener('click', () => { 
            idMidiaEmEdicao = null; 
            tipoMidiaAtual = null; 
            apiDataSelecionada = null; 
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
            apiDataSelecionada = null; 
        }
        
        modalAddMidia.classList.remove('escondido');
    }
    
    // Funções para estrelas do Modal ADICIONAR MÍDIA
    stars.forEach(star => {
        star.addEventListener('click', () => {
            const valor = star.dataset.value;
            setStars(valor);
        });
    });
    function setStars(valor) {
        midiaNotaInput.value = valor;
        stars.forEach(s => {
            s.textContent = (s.dataset.value <= valor) ? '★' : '☆';
        });
    }
    function resetarEstrelas() {
        setStars(0);
    }

    if(btnFecharAddMidia) { 
        btnFecharAddMidia.addEventListener('click', () => { 
            modalAddMidia.classList.add('escondido'); 
            idMidiaEmEdicao = null; 
            tipoMidiaAtual = null;
            apiDataSelecionada = null; 
            clearApiResults(); 
        }); 
    }

    // ===============================================
    // --- LÓGICA DO NOVO MODAL DE AVALIAÇÃO ---
    // ===============================================

    avaliacaoStars.forEach(star => {
        star.addEventListener('click', () => {
            const valor = star.dataset.value;
            setAvaliacaoStars(valor);
        });
    });

    function setAvaliacaoStars(valor) {
        avaliacaoNotaInput.value = valor;
        avaliacaoStars.forEach(s => {
            s.textContent = (s.dataset.value <= valor) ? '★' : '☆';
        });
    }

    if (btnAbrirAvaliacao) {
        btnAbrirAvaliacao.addEventListener('click', () => {
            if (!midiaAtualEmDetalhe) return;
            
            modalAvaliacaoTitulo.textContent = `Avaliar: ${midiaAtualEmDetalhe.nome}`;
            
            if (avaliacaoAtualDoUsuario) {
                setAvaliacaoStars(avaliacaoAtualDoUsuario.nota);
                avaliacaoComentarioInput.value = avaliacaoAtualDoUsuario.comentario || '';
            } else {
                setAvaliacaoStars(0);
                avaliacaoComentarioInput.value = '';
            }
            
            modalAddAvaliacao.classList.remove('escondido');
        });
    }
    
    if (btnFecharModalAvaliacao) {
        btnFecharModalAvaliacao.addEventListener('click', () => {
            modalAddAvaliacao.classList.add('escondido');
        });
    }

    if (btnSalvarAvaliacao) {
        btnSalvarAvaliacao.addEventListener('click', () => {
            if (!midiaAtualEmDetalhe) return;
            
            const payload = {
                midiaId: midiaAtualEmDetalhe.id,
                nota: parseInt(avaliacaoNotaInput.value, 10),
                comentario: avaliacaoComentarioInput.value
            };
            
            if (payload.nota === 0 && (payload.comentario === null || payload.comentario.trim() === '')) {
                alert("Por favor, adicione uma nota ou um comentário.");
                return;
            }

            fetch('/api/avaliacoes/salvar', {
                method: 'POST', 
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (response.status === 401) { throw new Error('Sessão expirada.'); }
                if (!response.ok) { throw new Error('Erro ao salvar avaliação.'); }
                return response.json();
            })
            .then(avaliacaoSalva => {
                alert('Avaliação salva com sucesso!');
                modalAddAvaliacao.classList.add('escondido');
                abrirModalDetalhes(midiaAtualEmDetalhe.id); 
                carregarFeedUsuario();
            })
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
        });
    }

    // ===============================================
    // --- FUNÇÕES DA LISTA DE RESULTADOS (API)
    // ===============================================

    function clearApiResults() {
        if(apiSearchResults) {
            apiSearchResults.innerHTML = '';
            apiSearchResults.classList.add('escondido');
        }
    }

    // === "populateFormWithApiData" ATUALIZADO ===
    function populateFormWithApiData(data) {
        if (tipoMidiaAtual === 'filme') {
            midiaDiretorInput.value = data.diretor || '';
            midiaAnoLancamentoInput.value = data.anoLancamento || '';
        } else if (tipoMidiaAtual === 'serie') {
            midiaGeneroSerieInput.value = data.genero || '';
        } else if (tipoMidiaAtual === 'livro') {
            midiaAutorInput.value = data.autor || '';
            midiaGeneroLivroInput.value = data.genero || '';
        } else if (tipoMidiaAtual === 'musica') {
            midiaArtistaInput.value = data.artista || '';
            midiaAlbumInput.value = data.album || '';
            // --- CAMPO ATUALIZADO ---
            midiaAnoEstreiaInput.value = data.anoEstreia || '';
        }
    }

    function displayApiResults(results) {
        clearApiResults();
        if (!results || results.length === 0) return;

        apiSearchResults.classList.remove('escondido');
        let hasValidResults = false;

        results.forEach(item => {
            if (!item.nome || item.nome === "N/A") {
                return; 
            }
            hasValidResults = true;
            const itemDiv = document.createElement('div');
            itemDiv.className = 'search-result-item';
            let nome, info, posterUrl;
            
            if (tipoMidiaAtual === 'filme' || tipoMidiaAtual === 'serie') {
                nome = item.nome;
                info = (item.diretor && item.diretor !== 'N/A') ? item.diretor : `Ano: ${item.anoLancamento || 'N/A'}`;
                posterUrl = item.posterUrl;
            } else if (tipoMidiaAtual === 'livro') {
                nome = item.nome;
                info = item.autor || 'Autor desconhecido';
                posterUrl = item.posterUrl;
            } else if (tipoMidiaAtual === 'musica') {
                nome = item.nome;
                info = item.artista || 'Artista desconhecido';
                posterUrl = item.posterUrl;
            }

            const imgTag = posterUrl 
                ? `<img src="${posterUrl}" alt="Poster">`
                : `<img src="https://via.placeholder.com/40x60/0d3d82/FFFFFF?text=?" alt="Poster">`;

            itemDiv.innerHTML = `
                ${imgTag}
                <div class="search-result-info">
                    <span class="search-result-title">${nome}</span>
                    <span class="search-result-info-sub">${info}</span>
                </div>
            `;
            itemDiv.dataset.apiData = JSON.stringify(item);
            apiSearchResults.appendChild(itemDiv);
        });

        if (!hasValidResults) {
            clearApiResults();
        }
    }

    if (apiSearchResults) {
        apiSearchResults.addEventListener('click', (e) => {
            const itemDiv = e.target.closest('.search-result-item');
            if (!itemDiv) return;
            const data = JSON.parse(itemDiv.dataset.apiData);
            apiDataSelecionada = data; 
            midiaNomeInput.value = data.nome;
            populateFormWithApiData(data);
            clearApiResults();
        });
    }

    // === FUNÇÕES DE TRIGGER (API) ATUALIZADAS ===
    function triggerFilmeSerieAutoFill() {
        if (idMidiaEmEdicao) return; 
        const nome = midiaNomeInput.value;
        const diretor = midiaDiretorInput.value; 
        
        if (nome.length < 2 && diretor.length < 2) { 
            clearApiResults();
            return;
        }
        
        let url = `/api/tmdb/buscar?query=${nome}&tipo=${tipoMidiaAtual}`;
        
        if (diretor && diretor.length > 1) { 
            url += `&diretor=${diretor}`;
        }
        
        fetch(url, { credentials: 'include' })
            .then(response => response.ok ? response.json() : [])
            .then(data => { displayApiResults(data); })
            .catch(error => { console.warn(error.message); clearApiResults(); });
    }
    function triggerLivroAutoFill() {
        if (idMidiaEmEdicao) return;
        const nome = midiaNomeInput.value;
        const autor = midiaAutorInput.value; 
        if (nome.length < 2 && autor.length < 2) {
            clearApiResults();
            return;
        } 
        let url = `/api/tmdb/buscar-livro?query=${nome}`;
        if (autor) { url += `&autor=${autor}`; }
        fetch(url, { credentials: 'include' })
            .then(response => response.ok ? response.json() : [])
            .then(data => { displayApiResults(data); })
            .catch(error => { console.warn(error.message); clearApiResults(); });
    }
    function triggerMusicaAutoFill() {
        if (idMidiaEmEdicao) return;
        const nome = midiaNomeInput.value;
        const artista = midiaArtistaInput.value;
        if (nome.length < 2 && artista.length < 2) { 
            clearApiResults();
            return;
        }
        let url = `/api/tmdb/buscar-musica?query=${nome}`;
        if (artista) { url += `&artista=${artista}`; }
        fetch(url, { credentials: 'include' })
            .then(response => response.ok ? response.json() : [])
            .then(data => { displayApiResults(data); })
            .catch(error => { console.warn(error.message); clearApiResults(); });
    }

    // ===============================================
    // === LISTENERS DE INPUT (COM DEBOUNCE) ===
    // ===============================================

    const handleApiSearch = () => {
        apiDataSelecionada = null; 
        
        if (tipoMidiaAtual === 'filme' || tipoMidiaAtual === 'serie') {
            triggerFilmeSerieAutoFill();
        } else if (tipoMidiaAtual === 'livro') { 
            triggerLivroAutoFill(); 
        } else if (tipoMidiaAtual === 'musica') { 
            triggerMusicaAutoFill(); 
        }
    };

    const debouncedApiSearch = debounce(handleApiSearch, 500);

    if(midiaNomeInput) {
        midiaNomeInput.addEventListener('input', debouncedApiSearch);
    }
    if(midiaArtistaInput) {
        midiaArtistaInput.addEventListener('input', debouncedApiSearch);
    }
    if(midiaAutorInput) {
        midiaAutorInput.addEventListener('input', debouncedApiSearch);
    }
    if(midiaDiretorInput) { 
        midiaDiretorInput.addEventListener('input', debouncedApiSearch);
    }


    // === "btnSalvarMidia" ATUALIZADO ===
    if(btnSalvarMidia) {
        btnSalvarMidia.addEventListener('click', () => {
            if (!tipoMidiaAtual) { 
                alert("Erro: Tipo de mídia desconhecido."); 
                return; 
            }
            
            clearApiResults(); 
            
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

            if (apiDataSelecionada && !idMidiaEmEdicao) { 
                payload.posterUrl = apiDataSelecionada.posterUrl || null;
                payload.sinopse = apiDataSelecionada.sinopse || null;
            } else if (!idMidiaEmEdicao) { 
                payload.posterUrl = null;
                payload.sinopse = null;
            }
            
            if (tipoMidiaAtual === 'filme') {
                payload.diretor = document.getElementById('midia-diretor').value;
                payload.anoLancamento = parseInt(document.getElementById('midia-ano-lancamento').value, 10) || 0; 
            } else if (tipoMidiaAtual === 'serie') {
                payload.genero = document.getElementById('midia-genero-serie').value;
            } else if (tipoMidiaAtual === 'musica') {
                payload.artista = document.getElementById('midia-artista').value;
                payload.album = document.getElementById('midia-album').value;
                // --- CAMPO ATUALIZADO ---
                payload.anoEstreia = parseInt(document.getElementById('midia-ano-estreia').value, 10) || 0; 
                
                if (apiDataSelecionada && !idMidiaEmEdicao) {
                    payload.posterUrl = apiDataSelecionada.posterUrl || null;
                    payload.sinopse = apiDataSelecionada.sinopse || null; // 'sinopse' (letra)
                }
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
                apiDataSelecionada = null; 
                carregarFeedUsuario(); 
            })
            .catch(error => { 
                console.error(error); 
                alert(error.message); 
                apiDataSelecionada = null; 
            });
        });
    }

}); // Fim do 'DOMContentLoaded'