// ESPERA O HTML CARREGAR ANTES DE EXECUTAR QUALQUER COISA
window.addEventListener('DOMContentLoaded', () => {

    // ===============================================
    // --- 1. PEGAR TODOS OS ELEMENTOS
    // ===============================================

    // --- Telas Principais
    const telaLogin = document.getElementById('tela-login');
    const telaCadastro = document.getElementById('tela-cadastro');
    const telaApp = document.getElementById('tela-app');

    // --- Botões (Login e Cadastro)
    const btnIrParaCadastro = document.getElementById('btn-ir-para-cadastro');
    const btnEntrarApp = document.getElementById('btn-entrar-app');
    const btnIrParaLogin = document.getElementById('btn-ir-para-login');
    const btnEnviarCadastro = document.getElementById('btn-enviar-cadastro');
    
    // --- Inputs de Cadastro (para a máscara e limpeza)
    const cadEmailInput = document.getElementById('cad-email');
    const cadNomeInput = document.getElementById('cad-nome');
    const cadSenhaInput = document.getElementById('cad-senha');
    const cadConfirmaSenhaInput = document.getElementById('cad-confirma-senha');
    const cadNascimentoInput = document.getElementById('cad-nascimento');

    // --- Elementos de Navegação do App (NOVOS) ---
    const btnLogoHome = document.getElementById('btn-logo-home'); // Logo "My Zone"
    const btnAvatarHome = document.getElementById('btn-avatar-home'); // Avatar (Snoopy)
    const btnAbrirPedidos = document.getElementById('btn-abrir-pedidos'); // Ícone de Amigos (com badge)
    const btnAbrirConfig = document.getElementById('btn-abrir-config'); // Ícone de Engrenagem

    // --- Telas Internas do App (Seções do Feed - NOVAS) ---
    const telaAtualizacoes = document.getElementById('tela-atualizacoes'); // Tela Principal (Usuário)
    const telaAtualizacoesAmigos = document.getElementById('tela-atualizacoes-amigos'); // Tela de Amigos
    const telaConfiguracoes = document.getElementById('tela-configuracoes');

    // --- Popup Pedidos de Amizade (NOVO) ---
    const popupPedidosAmizade = document.getElementById('popup-pedidos-amizade');
    const btnAceitarAmigo = document.getElementById('btn-aceitar-amigo');
    const btnRecusarAmigo = document.getElementById('btn-recusar-amigo');

    // --- Menu de Perfil e Modal de Edição
    const btnMenuPerfil = document.getElementById('btn-menu-perfil');
    const menuPerfil = document.getElementById('menu-perfil');
    const btnAtualizarFoto = document.getElementById('btn-atualizar-foto');
    const btnAtualizarDados = document.getElementById('btn-atualizar-dados');
    const inputFotoPerfil = document.getElementById('input-foto-perfil');
    const modalEditarPerfil = document.getElementById('modal-editar-perfil');
    const btnFecharModal = document.getElementById('btn-fechar-modal');
    const btnSalvarDados = document.getElementById('btn-salvar-dados');

    // --- Modal de Apagar Conta
    const btnAbrirModalApagar = document.getElementById('btn-abrir-modal-apagar');
    const modalApagarConta = document.getElementById('modal-apagar-conta');
    const btnFecharModalApagar = document.getElementById('btn-fechar-modal-apagar');
    const btnConfirmarApagar = document.getElementById('btn-confirmar-apagar');

    // --- Configurações (Toggle de Privacidade)
    const togglePrivacyInput = document.getElementById('toggle-privacy');
    const privacyToggleLabel = document.querySelector('.privacy-toggle .privacy-text');
    const privacyToggleIcon = document.querySelector('.privacy-toggle .icon-lock');

    // --- Modal "Tipo de Mídia" (Botão '+') (NOVO) ---
    const btnAbrirTipoMidia = document.getElementById('btn-abrir-tipo-midia'); // Botão '+'
    const modalTipoMidia = document.getElementById('modal-tipo-midia');
    const btnFecharTipoMidia = document.getElementById('btn-fechar-tipo-midia');
    const botoesTipoMidia = document.querySelectorAll('.btn-tipo-midia');

    // --- Modal "Adicionar Mídia" (Formulário) (NOVO) ---
    const modalAddMidia = document.getElementById('modal-add-midia');
    const btnFecharAddMidia = document.getElementById('btn-fechar-add-midia');
    const btnSalvarMidia = document.getElementById('btn-salvar-midia');
    const addMidiaTitle = document.getElementById('add-midia-title');
    const camposDinamicos = document.querySelectorAll('.add-midia-form .form-group[data-tipo]');
    const addMidiaForm = document.querySelector('.add-midia-form');
    const stars = document.querySelectorAll('.star-rating .star');
    const midiaNotaInput = document.getElementById('midia-nota');

    
    // ===============================================
    // --- 2. FUNÇÕES DE AJUDA (Helpers)
    // ===============================================
    
    /**
     * Converte uma data de "DD/MM/AAAA" para "AAAA-MM-DD".
     * Retorna null se o formato for inválido.
     */
    function formatarDataParaAPI(data) {
        const partes = data.split('/');
        if (partes.length !== 3 || partes[2].length < 4) {
            return null; // Formato inválido
        }
        return `${partes[2]}-${partes[1]}-${partes[0]}`;
    }

    
    // ===============================================
    // --- 3. LÓGICA DE NAVEGAÇÃO E API
    // ===============================================

    // --- Navegação (Login / Cadastro)
    btnIrParaCadastro.addEventListener('click', () => { telaLogin.classList.add('escondido'); telaCadastro.classList.remove('escondido'); });
    btnIrParaLogin.addEventListener('click', () => { telaCadastro.classList.add('escondido'); telaLogin.classList.remove('escondido'); });
    
    // --- Máscara de Data (DD/MM/AAAA)
    if(cadNascimentoInput) {
        cadNascimentoInput.addEventListener('input', (e) => {
            let valor = e.target.value.replace(/\D/g, ''); // Remove tudo que não for número
            if (valor.length > 4) {
                valor = valor.slice(0, 2) + '/' + valor.slice(2, 4) + '/' + valor.slice(4, 8);
            } else if (valor.length > 2) {
                valor = valor.slice(0, 2) + '/' + valor.slice(2, 4);
            }
            e.target.value = valor;
        });
    }

    // --- API: Botão ENVIAR CADASTRO (Lógica mesclada)
    btnEnviarCadastro.addEventListener('click', () => {
        const email = cadEmailInput.value;
        const nome = cadNomeInput.value;
        const senha = cadSenhaInput.value;
        const senhaConfirmacao = cadConfirmaSenhaInput.value;
        const dataNascimentoInput = cadNascimentoInput.value;

        if (!email || !nome || !senha || !senhaConfirmacao || !dataNascimentoInput) {
            alert("Por favor, preencha todos os campos.");
            return;
        }
        if (senha !== senhaConfirmacao) {
            alert("As senhas não conferem!");
            return;
        }
        const dataNascimentoFormatada = formatarDataParaAPI(dataNascimentoInput);
        if (!dataNascimentoFormatada) {
            alert("Formato de data inválido. Por favor, use DD/MM/AAAA.");
            return;
        }

        const dadosCadastro = {
            nome: nome,
            email: email,
            senha: senha,
            dataNascimento: dataNascimentoFormatada
        };

        fetch('/api/usuarios/cadastro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosCadastro)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error('Erro ao cadastrar: ' + text) });
            }
            return response.json(); 
        })
        .then(usuarioSalvo => {
            alert('Usuário cadastrado com sucesso! ID: ' + usuarioSalvo.id);
            cadEmailInput.value = '';
            cadNomeInput.value = '';
            cadSenhaInput.value = '';
            cadConfirmaSenhaInput.value = '';
            cadNascimentoInput.value = '';
            
            telaCadastro.classList.add('escondido');
            telaLogin.classList.remove('escondido');
        })
        .catch(error => {
            console.error(error);
            alert(error.message);
        });
    });

    // --- API: Botão ENTRAR (Login) (Lógica mesclada)
    btnEntrarApp.addEventListener('click', () => {
        const email = document.getElementById('login-email').value;
        const senha = document.getElementById('login-senha').value;
    
        if (email === "" || senha === "") {
            alert('Por favor, digite seu e-mail e senha.');
            return;
        }
    
        const dadosLogin = { email: email, senha: senha };
    
        fetch('/api/usuarios/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosLogin)
        })
        .then(response => {
            if (response.status === 401) { throw new Error('E-mail ou senha inválidos.'); }
            if (!response.ok) { throw new Error('Erro ao tentar fazer login.'); }
            return response.json();
        })
        .then(usuarioLogado => {
            alert('Login bem-sucedido! Bem-vindo(a), ' + usuarioLogado.nome);
        
            document.getElementById('login-email').value = '';
            document.getElementById('login-senha').value = '';
        
            telaLogin.classList.add('escondido');
            telaCadastro.classList.add('escondido');
            telaApp.classList.remove('escondido');
        
            // Atualiza a tela principal com os dados do usuário
            document.querySelector('.profile-user-info').textContent = usuarioLogado.nome;
            // (Você pode adicionar um campo 'bio' no Usuario.java e popular aqui também)
            // document.querySelector('.profile-bio-box').textContent = usuarioLogado.bio || 'Essa é a minha Bio :D';
            
            // Garante que a tela principal (atualizações do usuário) seja a visível
            esconderTelasApp();
            telaAtualizacoes.classList.remove('escondido');
        })
        .catch(error => {
            alert(error.message);
        });
    });
    

    // --- Navegação (Dentro do App) (Lógica NOVA) ---
    function esconderTelasApp() {
        if(telaAtualizacoes) telaAtualizacoes.classList.add('escondido');
        if(telaAtualizacoesAmigos) telaAtualizacoesAmigos.classList.add('escondido');
        if(telaConfiguracoes) telaConfiguracoes.classList.add('escondido');
    }

    if(btnAvatarHome) {
        btnAvatarHome.addEventListener('click', () => {
            esconderTelasApp();
            telaAtualizacoes.classList.remove('escondido');
        });
    }

    if(btnLogoHome) {
        btnLogoHome.addEventListener('click', () => {
            esconderTelasApp();
            telaAtualizacoesAmigos.classList.remove('escondido');
        });
    }

    if(btnAbrirConfig) {
        btnAbrirConfig.addEventListener('click', () => {
            esconderTelasApp();
            telaConfiguracoes.classList.remove('escondido');
        });
    }

    if(btnAbrirPedidos) {
        btnAbrirPedidos.addEventListener('click', (e) => {
            e.stopPropagation(); 
            if(popupPedidosAmizade) popupPedidosAmizade.classList.remove('escondido');
        });
    }

    // --- Lógica (Pedidos de Amizade) (Lógica NOVA)
    if(btnAceitarAmigo) {
        btnAceitarAmigo.addEventListener('click', () => { 
            popupPedidosAmizade.classList.add('escondido'); 
            alert('Pedido aceito!'); 
        });
    }
    if(btnRecusarAmigo) {
        btnRecusarAmigo.addEventListener('click', () => { 
            popupPedidosAmizade.classList.add('escondido'); 
            alert('Pedido recusado.'); 
        });
    }
    
    document.addEventListener('click', (e) => {
        // Fecha o popup de amizade se clicar fora dele
        if(popupPedidosAmizade && !popupPedidosAmizade.contains(e.target) && e.target !== btnAbrirPedidos) {
            popupPedidosAmizade.classList.add('escondido');
        }

        // Fecha o menu de perfil se clicar em qualquer outro lugar
        if(menuPerfil && !menuPerfil.contains(e.target) && e.target !== btnMenuPerfil) {
             menuPerfil.classList.add('escondido'); 
        }
    });

    // --- Lógica (Menu de Perfil e Modais de Edição)
    if(btnMenuPerfil) {
        btnMenuPerfil.addEventListener('click', (e) => { 
            e.stopPropagation(); 
            menuPerfil.classList.toggle('escondido'); 
        });
    }
    if(btnAtualizarFoto) {
        btnAtualizarFoto.addEventListener('click', () => { 
            menuPerfil.classList.add('escondido'); 
            inputFotoPerfil.click(); 
        });
    }
    if(inputFotoPerfil) {
        inputFotoPerfil.addEventListener('change', (e) => { 
            if (e.target.files && e.target.files[0]) { 
                alert('Nova foto selecionada: ' + e.target.files[0].name); 
            } 
        });
    }
    if(btnAtualizarDados) {
        btnAtualizarDados.addEventListener('click', () => { 
            menuPerfil.classList.add('escondido'); 
            modalEditarPerfil.classList.remove('escondido'); 
        });
    }
    if(btnFecharModal) {
        btnFecharModal.addEventListener('click', () => { 
            modalEditarPerfil.classList.add('escondido'); 
        });
    }
    if(btnSalvarDados) {
        btnSalvarDados.addEventListener('click', () => { 
            alert('Dados enviados para atualização! (fictício)'); 
            modalEditarPerfil.classList.add('escondido'); 
        });
    }

    // --- Lógica (Configurações e Apagar Conta)
    if(togglePrivacyInput) {
        togglePrivacyInput.addEventListener('change', () => {
            if (togglePrivacyInput.checked) {
                privacyToggleLabel.textContent = 'PÚBLICO'; 
                privacyToggleIcon.textContent = '🔓';
            } else {
                privacyToggleLabel.textContent = 'PRIVADO'; 
                privacyToggleIcon.textContent = '🔒';
            }
        });
    }
    if(btnAbrirModalApagar) {
        btnAbrirModalApagar.addEventListener('click', () => { 
            modalApagarConta.classList.remove('escondido'); 
        });
    }
    if(btnFecharModalApagar) {
        btnFecharModalApagar.addEventListener('click', () => { 
            modalApagarConta.classList.add('escondido'); 
        });
    }
    if(btnConfirmarApagar) {
        btnConfirmarApagar.addEventListener('click', () => {
            modalApagarConta.classList.add('escondido');
            alert('Conta apagada com sucesso! (Fictício)');
            
            telaApp.classList.add('escondido');
            telaLogin.classList.remove('escondido');
        });
    }

    // --- Lógica (Adicionar Mídia - Botão '+') (Lógica NOVA)
    if(btnAbrirTipoMidia) {
        btnAbrirTipoMidia.addEventListener('click', () => {
            modalTipoMidia.classList.remove('escondido');
        });
    }

    if(btnFecharTipoMidia) {
        btnFecharTipoMidia.addEventListener('click', () => {
            modalTipoMidia.classList.add('escondido');
        });
    }

    botoesTipoMidia.forEach(botao => {
        botao.addEventListener('click', () => {
            const tipo = botao.dataset.tipo;
            if (tipo === 'nova') {
                alert('Função "Nova Categoria" não implementada.');
                return;
            }
            abrirModalAddMidia(tipo, botao.textContent.trim());
        });
    });

    function abrirModalAddMidia(tipo, nomeTipo) {
        modalTipoMidia.classList.add('escondido');
        const icones = { 'musica': '🎵', 'filme': '🎬', 'serie': '📺', 'livro': '📖' };
        addMidiaTitle.textContent = `${icones[tipo] || '📝'} Novo(a) ${nomeTipo}`;
        
        camposDinamicos.forEach(campo => { campo.style.display = 'none'; });
        
        const camposParaMostrar = document.querySelectorAll(`.form-group[data-tipo="${tipo}"]`);
        camposParaMostrar.forEach(campo => { campo.style.display = 'block'; });
        
        if(addMidiaForm) addMidiaForm.reset(); 
        resetarEstrelas(); 
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
        });
    }

    if(btnSalvarMidia) {
        btnSalvarMidia.addEventListener('click', () => {
            // (Aqui entrará o FETCH para salvar a mídia no futuro)
            const nome = document.getElementById('midia-nome').value;
            if (nome === "") {
                alert('O campo "Nome" é obrigatório!');
                return;
            }
            alert('Mídia salva com sucesso! (Fictício)');
            modalAddMidia.classList.add('escondido');
        });
    }

}); // Fim do 'DOMContentLoaded'