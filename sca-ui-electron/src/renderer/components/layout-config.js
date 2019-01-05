const components = {
	projectTree: () => {
		return {
			title: 'Project Tree',
			type: 'component',
			componentName: 'project-tree',
			componentState: {
				id: 'project-tree'
			},
			isClosable: false
		}
	},
	projectFiles: () => {
		return {
			title: 'Project Files',
			type: 'component',
			componentName: 'project-files',
			componentState: {
				id: 'project-files'
			},
			isClosable: false
		}
	},
	editor: (id) => {
		return {
			title: 'Editor ' + id,
			type: 'component',
			componentName: 'editor',
			componentState: {
				id: id,
				text: '% Editor ' + id
			}
		}
	},
	logView: () =>  {
		return {
			title: "Message Log",
			type: 'component',
			componentName: 'log-view',
			componentState: {
				id: 'ConsoleLog',
				text: '[INFO] LOG START'
			},
			isClosable: false,
			height: 40
		}
	},
	lexicon: (name, data) => {
		return {
			title: name,
			type: 'component',
			componentName: 'lexicon',
			componentState: {
				id: 'Lexicon' + name,
				text: data
			}
		}
	}
};

module.exports = {
	content: [{
		type: 'row',
		content: [{
			id: 'project',
			isClosable: false,
			type: 'column',
			width: 20,
			content: [
				components.projectTree(),
				components.projectFiles(),
			]
		}, {
			type: 'column',
			content: [
				{
					type: 'row',
					content: [{
						id: 'editors',
						isClosable: false,
						type: 'stack',
						content: [{
							title: 'fuck',
							type: 'component',
							componentName: 'editor',
							componentState: {
								id: 'id',
								text: '% PIE to Proto-Kuma-Kuban Rules\n' +
									'% Haedus Toolbox SCA Rules File\n' +
									'% Samantha Fiona Morrigan McCabe\n' +
									'\n' +
									'mode intelligent\n' +
									'\n' +
									'% Set the normalization mode before loading the lexicon\n' +
									'open \'pie_lexicon.txt\' as LEXICON\n' +
									'\n' +
									'% Normalize [i u] vs [y w] use\n' +
									'y w > i u\n' +
									'\n' +
									'C P T K V L > k p t k a r\n' +
									'\n' +
									'ā́  ḗ  ī́  ṓ  ū́  > â ê î ô û\n' +
									'\n' +
									'% For the sake of this example, we will not deal with stress\n' +
									'% Which is not consistently marked in our data anyway\n' +
									'á é í ó ú > a e i o u\n' +
									'â ê î ô û > ā ē ī ō ū\n' +
									'\n' +
									'E = e ē\n' +
									'A = a ā\n' +
									'O = o ō\n' +
									'I = i ī\n' +
									'U = u ū\n' +
									'\n' +
									'H = x ʔ\n' +
									'N = m n\n' +
									'L = r l\n' +
									'R = N L\n' +
									'W = y w\n' +
									'\n' +
									'Q = kʷʰ kʷ gʷ\n' +
									'K = kʰ  k  g\n' +
									'P = pʰ  p  b\n' +
									'T = tʰ  t  d\n' +
									'\n' +
									'[Pls] = P T K Q\n' +
									'[Obs] = [Pls] s\n' +
									'C = [Obs] R W\n' +
									'\n' +
									'VS  = a e i o u\n' +
									'VL  = ā ē ī ō ū\n' +
									'LOW = A E O\n' +
									'V   = VS VL \n' +
									'\n' +
									'% Szemerenyi\'s law\n' +
									'Z = R s t nt\n' +
									'VSm-m VSZ-s > VLm VLZ / _#\n' +
									'\n' +
									'% For correct handling of negation prefix\n' +
									'n- > nˌ / #_\n' +
									'\n' +
									'% Delete morpheme boundary marking\n' +
									'- > 0\n' +
									'\n' +
									'% This is bad design, maybe we can fix the data\n' +
									'ee eo oe ea ae > ē ō ō ā ā\n' +
									'\n' +
									'% --- PHASE I -----------------------------------------------------------------\n' +
									'\n' +
									'h₁ h₂ h₃ h₄ > ʔ x ɣ ʕ\n' +
									'hₓ hₐ > ʔ ʕ\n' +
									'\n' +
									'E > A / {x ʕ}_ or _{x ʕ}\n' +
									'E > O / ɣ_ or _ɣ\n' +
									'ɣ ʕ > x ʔ\n' +
									'\n' +
									'ǵʰ ǵ ḱ > gʰ g k % Centum, loss of palatovelars\n' +
									'\n' +
									'% h₂ and h₃ cause aspiration of plosives\n' +
									'b d g gʷ  > bʰ dʰ gʰ gʷʰ / _{x ɣ}\n' +
									'p t k kʷ  > pʰ tʰ kʰ kʷʰ / _{x ɣ}\n' +
									'\n' +
									'% Grassman\'s Law should apply here, actually\n' +
									'[Aspirata]   = bʰ dʰ gʰ gʷʰ pʰ tʰ kʰ kʷʰ\n' +
									'[Anaspirata] = b  d  g  gʷ  p  t  k  kʷ\n' +
									'\n' +
									'[Aspirata] > [Anaspirata] / _{R W}?VV?C*[Aspirata]\n' +
									'\n' +
									'bʰ dʰ gʰ gʷʰ > pʰ tʰ kʰ kʷʰ\n' +
									'\n' +
									'H > 0 / _{H i y}\n' +
									'\n' +
									'iH uH > ī ū\n' +
									'\n' +
									'au eu ou am em om > ā ē ō ā ē ō / _m#\n' +
									'\n' +
									'% --- PHASE II ----------------------------------------------------------------\n' +
									'\n' +
									'mr wr ml wl > br br bl bl / #_V\n' +
									'\n' +
									'mH nH > mə nə / #_C\n' +
									'\n' +
									'% Originally, we did this with two rules. However, the first one\n' +
									'%\t\trH lH mH nH > rˌH lˌH mˌH nˌH / [Obs]_\n' +
									'% can have H moved into the condition, at which point it\'s \n' +
									'%\t\tr l m n > rˌ lˌ mˌ nˌ / [Obs]_H\n' +
									'% which is compatible with another old rule:\n' +
									'%\t\tr l m n > rˌ lˌ mˌ nˌ / [Obs]_{C #}\n' +
									'\n' +
									'r l m n > rˌ lˌ mˌ nˌ / [Obs]_{H C #}\n' +
									'\n' +
									'n m > nˌ mˌ / C_#\n' +
									'\n' +
									'rˌ lˌ > r l / C_{mˌ nˌ}{C #}\n' +
									'\n' +
									'nˌH mˌH > əˉ      / _C\n' +
									'nˌH mˌH > əˉn əˉm / _{V #}\n' +
									'rˌH lˌH > əˉr əˉl / _{C #} or _V\n' +
									'\n' +
									'% Desyllabification\n' +
									'nˌ mˌ > ə     / _C\n' +
									'nˌ mˌ > ən əm / _{V #}\n' +
									'rˌ  lˌ  > ər  əl\n' +
									'\n' +
									'ə əˉ > u ū / {K Q}_\n' +
									'\n' +
									'kʰ k g > cʰ c ɟ / _{I E y}\n' +
									'\n' +
									'% LABIOVELAL SPLIT\n' +
									'Q > K / {U w}_ or _{O U w [Obs]}\n' +
									'Q > P\n' +
									'\n' +
									'% --- PHASE III ---------------------------------------------------------------\n' +
									'\n' +
									'% These could also be merged, which is made more interesting\n' +
									'% with its resulting nested sets \n' +
									'% VS > VL / _H{C #}\n' +
									'% VS > VL / _H{I U W}?V\n' +
									'\n' +
									'VS > VL / _H{C # {I U W}?V}\n' +
									'\n' +
									'H > 0 / {[Obs] R}_V\n' +
									'H > 0 /         R_R\n' +
									'H > 0 /        VL_{C # u}\n' +
									'\n' +
									'xa xə > 0 / VL_\n' +
									'xu xo > u / VL_\n' +
									'\n' +
									'% Desyllabification of Semivowels\n' +
									'i u > y w / _VC\n' +
									'iuiH uiuH > iwī uyū / C_\n' +
									'\n' +
									'i u > y w / #_{I U}H\n' +
									'i u > y w / LOW_VL or _LOW\n' +
									'i u > y w / {H C}_V\n' +
									'\n' +
									'y w > i u / _{C H #}\n' +
									'\n' +
									'VSī VSū > VLi VLu / _{C #}\n' +
									'VSī VSū > VLy VLw / _V\n' +
									'\n' +
									'H > ə / {# [Obs]}_C\n' +
									'H > ə / C_# or R_R#\n' +
									'\n' +
									'H > 0 / _{C #} or C_\n' +
									'ʔ > 0 / #_\n' +
									'\n' +
									'iʔə uʔə > ī ū % Does this even occur?\n' +
									'Hə > 0 / VL_\n' +
									'\n' +
									'% Consonant Clusters\n' +
									'Ks > ks\n' +
									'% tk tʰkʰ  > ks ks\n' +
									'\n' +
									's > 0 / _ss*\n' +
									'\n' +
									'oʔLOW LOWʔo > ō\n' +
									'\n' +
									'ʔ > 0 / LOW_{I U}\n' +
									'\n' +
									'eʔe aʔa eʔa aʔe > ē ā ā ē\n' +
									'\n' +
									'% Rules like this are to be avoided in this language nad probably\n' +
									'% Indicative that there is a problem somewhere\n' +
									'EU EI > Eu Ei\n' +
									'AU AI > Au Ai\n' +
									'OU OI > Ou Oi\n' +
									'\n' +
									'aʔ eʔ oʔ > ā ē ō / _{I U W}\n' +
									'\n' +
									'ēʔə ēʔe > ē\n' +
									'āʔe āʔa āxa > ā\n' +
									'\n' +
									'oi ōi eu ēu > ai ei au ou\n' +
									'\n' +
									'% Siever\'s law for Nasals\n' +
									'n m > ən əm / CC+_V\n' +
									'y w > iy uw / CC+_V\n' +
									'\n' +
									'% This may be a bit arbitrary\n' +
									'əˉ ə > ū u / K_\n' +
									'\n' +
									'\n' +
									'close LEXICON as \'pkk_lexicon.txt\'\n',
							}
						}]
					}]
				},
				components.logView()
			]
		}]
	}]
};